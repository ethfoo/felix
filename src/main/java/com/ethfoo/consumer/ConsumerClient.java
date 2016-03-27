package com.ethfoo.consumer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

import com.ethfoo.registry.AddressProvider;
import com.ethfoo.serializer.Decoder;
import com.ethfoo.serializer.Encoder;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ConsumerClient implements InitializingBean{
	private String host;
	private int port;
	private EventLoopGroup group;
	private Channel channel;
	
	private ConcurrentMap<String, RpcFuture> rpcFutureMap = new ConcurrentHashMap<String, RpcFuture>(); 
	
	public ConsumerClient(AddressProvider addressProvider){
		host = addressProvider.getHost();
		port = addressProvider.getPort();
	}
	
	/*
	 * 在初始化时建立连接
	 */
	public void afterPropertiesSet() throws Exception {
		group = new NioEventLoopGroup();
		final EventLoopGroup handlerResponseGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors()*2);
		
		try{
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.SO_KEEPALIVE, true)
			 .handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new Decoder(Response.class))
					.addLast(new Encoder(Request.class))
					.addLast(handlerResponseGroup, new ConsumerClientHandler(rpcFutureMap));	
					
				}
				 
			 });
			
			//TODO 以后尝试使用连接池连接，加入心跳检测
			
			System.out.println("connected to server" + ",server host:" + host + ", port:" + port);
			channel = b.connect(host, port).sync().channel();
			
		}catch(Exception exception){
			exception.printStackTrace();
		}
	}
	
	/*
	 * 选择一个channel，发送request
	 */
	public RpcFuture send(Request request){
		RpcFuture rpcFuture = new RpcFuture();
		rpcFutureMap.put(request.getRequestId(), rpcFuture);
		if( channel != null){
			channel.writeAndFlush(request);
		}
		return rpcFuture;
	}




}
