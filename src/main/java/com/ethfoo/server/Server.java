package com.ethfoo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.ethfoo.annotation.RpcProvider;
import com.ethfoo.registry.AddressProvider;
import com.ethfoo.serializer.Decoder;
import com.ethfoo.serializer.Encoder;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class Server implements ApplicationContextAware, InitializingBean{
	private AddressProvider addressProvider;
	Map<String, Object> exportClassMap = new HashMap<String, Object>();
	public Server(AddressProvider addressProvider){
		this.addressProvider = addressProvider;
	}

	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		Map<String, Object> beanMap = ctx.getBeansWithAnnotation(RpcProvider.class);
		if( !beanMap.isEmpty() ){
			for(Object bean : beanMap.values()){
				String interfaceName = bean.getClass().getAnnotation(RpcProvider.class).value().getName();
				System.out.println("interfaceName: " + interfaceName);
				exportClassMap.put(interfaceName, bean);
			}
		}
	}
	
	public void afterPropertiesSet() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); 
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, workerGroup)
				 	 .channel(NioServerSocketChannel.class)
				 	 .option(ChannelOption.SO_BACKLOG, 100)
				 	 .option(ChannelOption.SO_REUSEADDR, true)
				 	 .childOption(ChannelOption.SO_KEEPALIVE, true)
				 	 .childHandler(new ChannelInitializer<SocketChannel>(){

						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							ch.pipeline().addLast(new Encoder(Response.class))
										 .addLast(new Decoder(Request.class))
										 .addLast(new ServerHandler(exportClassMap));
						}
				 		 
				 	 });
			
			ChannelFuture future = bootstrap.bind(addressProvider.getPort()).sync();
			System.out.println("start server...");
			
			future.channel().closeFuture().sync();
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	


	
}
