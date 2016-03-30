package com.ethfoo.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.InitializingBean;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import com.ethfoo.aop.RpcInvokeHook;
import com.ethfoo.async.RpcFuture;
import com.ethfoo.registry.AddressProvider;
import com.ethfoo.serializer.Decoder;
import com.ethfoo.serializer.Encoder;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ConsumerClient implements InitializingBean{
	private AddressProvider addressProvider;
	private String host;
	private int port;
	private EventLoopGroup group;
	private Channel channel;
	private RpcInvokeHook hook;
	private final AtomicInteger index = new AtomicInteger();
	private List<Channel> channelList = new ArrayList<Channel>();
	private int connCount = 1;
	private Bootstrap bootstrap;
	private static final int IDLE_TIME = 5;
	
	private ConcurrentMap<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<String, RpcFuture>(); 
	
	public ConsumerClient(AddressProvider addressProvider){
		this.addressProvider = addressProvider;
		host = addressProvider.getHost();
		port = addressProvider.getPort();
	}
	
	public void setHook(RpcInvokeHook hook) {
		this.hook = hook;
	}

	public void setConnCount(int connCount) {
		this.connCount = connCount;
		System.out.println("the connections count is: " + connCount);
	}

	/*
	 * 在初始化时建立连接
	 */
	public void afterPropertiesSet() throws Exception {
		group = new NioEventLoopGroup();
		final EventLoopGroup handlerResponseGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2);
		
		try{
			bootstrap = new Bootstrap();
			bootstrap.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.SO_KEEPALIVE, true)
			 .handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new Decoder(Response.class))
					.addLast(new Encoder(Request.class))
					.addLast(new IdleStateHandler(0, 0, IDLE_TIME))
					.addLast(new ClientHeartBeatHandler(bootstrap, channelList, addressProvider))
					.addLast(handlerResponseGroup, new ConsumerClientHandler(rpcFutureMap,hook));	
					
				}
				 
			 });
			
			
			System.out.println("connected to server" + ",server host:" + host + ", port:" + port);
			for( int i=0; i<connCount; i++){
				channel = bootstrap.connect(host, port).channel();
				channelList.add(channel);
			}
			
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	/*
	 * 选择一个channel，发送request
	 */
	public RpcFuture send(Request request){
		if( hook != null){
			hook.beforeInvoke(request);
		}
		
		RpcFuture rpcFuture = new RpcFuture();
		rpcFutureMap.put(request.getRequestId(), rpcFuture);
		nextChannel().writeAndFlush(request);
		return rpcFuture;
	}
	
	public Channel nextChannel(){
		return getFirstChannelActive(0);
	}

	private Channel getFirstChannelActive(int count){
		Channel channel = channelList.get(Math.abs(index.getAndIncrement()%channelList.size()));
		
		//如果该channel为inActive，则重连并递归的选择下一个channel
		if( !channel.isActive() ){
			reconnect(channel);
			
			//如果所有的channel都已被用完，抛出异常
			if( count > channelList.size() ){
				throw new RuntimeException("no channel can be use");
			}
			
			return getFirstChannelActive(count + 1);
		}
		
		return channel;
	}
	
	private void reconnect(Channel channel) {
		//重连的时候有可能该channel被其他线程使用新的可使用的channel替换
		synchronized(channel){
			if( channelList.indexOf(channel) == -1){
				return;
			}
		}
		Channel newChannel = bootstrap.connect(host, port).channel();
		channelList.set(channelList.indexOf(channel), newChannel);
	}

}
