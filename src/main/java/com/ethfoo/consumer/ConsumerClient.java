package com.ethfoo.consumer;

import java.util.concurrent.ExecutionException;

import io.netty.bootstrap.Bootstrap;
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

import com.ethfoo.serializer.Decoder;
import com.ethfoo.serializer.Encoder;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ConsumerClient extends SimpleChannelInboundHandler<Response>{
	private String host;
	private int port;
	
	private Response response;

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
					.addLast(new Decoder(Response.class))
					.addLast(new Encoder(Request.class))
					.addLast(ConsumerClient.this);	
					
				}
				 
			 });
			
			ChannelFuture future = b.connect(host, port).sync();
			future.channel().writeAndFlush(request).sync();
			
			synchronized(lock){
				lock.wait();
			}
			
		}finally{
			group.shutdownGracefully();
		}
		
		
		return response;
	}


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response response)
			throws Exception {
		this.response = response;
		
		synchronized(lock){
			lock.notifyAll();
		}
	}
}
