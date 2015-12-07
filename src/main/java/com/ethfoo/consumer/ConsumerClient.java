package com.ethfoo.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ConsumerClient {
	private String host;
	private int port;

	private byte[] lock = new byte[1];
	
	public ConsumerClient(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	
	public Response send(Request request) throws InterruptedException{
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group)
			 .channel(NioSocketChannel.class)
			 .option(ChannelOption.SO_KEEPALIVE, true)
			 .handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline()
					.addLast(new ConsumerClientHandler());	//TODO 添加编解码、添加客户端处理
					
				}
				 
			 });
			
			ChannelFuture future = b.connect(host, port).sync();
			future.channel().writeAndFlush(request).sync();
			
			
			
			
			
			
		}finally{
			group.shutdownGracefully();
		}
		
		
		return null;
	}
}
