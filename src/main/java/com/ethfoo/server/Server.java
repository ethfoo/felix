package com.ethfoo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

import com.ethfoo.registry.AddressProvider;

public class Server {
	private AddressProvider addressProvider;
	private Map<String, Object> exportClassMap;
	
	
	public Server(AddressProvider addressProvider){
		this.addressProvider = addressProvider;
	}
	
	public void export(Map exportClassMap) throws Exception{
		
		EventLoopGroup bossGroup = new NioEventLoopGroup(); 
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
				 	 .channel(NioServerSocketChannel.class)
				 	 .option(ChannelOption.SO_BACKLOG, 100)
				 	 .childOption(ChannelOption.SO_KEEPALIVE, true)
				 	 .childHandler(new ChannelInitializer<SocketChannel>(){

						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast();
						}
				 		 
				 	 });
			
			ChannelFuture future = bootstrap.bind(addressProvider.getHost(),
												  addressProvider.getPort()).sync();
			
			future.channel().closeFuture().sync();
			
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
}
