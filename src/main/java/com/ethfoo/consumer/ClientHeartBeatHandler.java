package com.ethfoo.consumer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

import com.ethfoo.registry.AddressProvider;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ClientHeartBeatHandler extends SimpleChannelInboundHandler<Response>{
	private int beatFailCount = 0;
	private static final int MAX_FAIL_COUNT = 5; 
	private static final int RECONN_TIME = 5;
	//private AddressProvider addressProvider;
	private Bootstrap bootstrap;
	private List<Channel> channelList;
	private AddressProvider addressProvider;
	
	public ClientHeartBeatHandler(Bootstrap bootstrap, List<Channel> channelList, AddressProvider addressProvider){
		this.bootstrap = bootstrap;
		this.channelList = channelList;
		this.addressProvider = addressProvider; 
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response msg)
			throws Exception {

		if( msg.isHeartBeat() ){
			beatFailCount = 0;
			System.out.println("client receive pong");
		}else{
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("client channel inactive, this channel is: " + ctx.channel().toString());
		ctx.executor().scheduleAtFixedRate(new Runnable(){

			public void run() {
				System.out.println("reconnect...");
				//client.nextChannel();
				reconnect(ctx.channel());
			}
			
		}, 0, RECONN_TIME, TimeUnit.SECONDS);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {

		if( evt instanceof IdleStateEvent ){
			beatFailCount++;
			System.out.println("空闲时间达到5s, beatFailCount=" + beatFailCount);
			if( beatFailCount < MAX_FAIL_COUNT ){
				System.out.println("send ping");
				Request request = new Request();
				request.setHeartBeat(true);
				ctx.writeAndFlush(request);
			}else{
				System.out.println("close channel");
				ctx.channel().close();
			}
		}else{
			ctx.fireUserEventTriggered(evt);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
	
	}


	private void reconnect(Channel channel) {
		//重连的时候有可能该channel被其他线程使用新的可使用的channel替换
		/*synchronized(channel){
			if( channelList.indexOf(channel) == -1){
				System.out.println("channelList.indexOf(channel) == -1");
				return;
			}
		}*/
		
		/*Channel newChannel = bootstrap.connect(addressProvider.getHost(), addressProvider.getPort()).channel();
		System.out.println("client 重新连接到: " + addressProvider.getHost() + ", " + addressProvider.getPort());
		channelList.set(channelList.indexOf(channel), newChannel);
		*/
		channelList.clear();
		for( int i=0; i<1; i++){
			channel = bootstrap.connect(addressProvider.getHost(), addressProvider.getPort()).channel();
			
			System.out.println("client 尝试重新连接 : " + addressProvider.getHost() + ", " + addressProvider.getPort());
			channelList.add(channel);
		}
	}

	

	
}
