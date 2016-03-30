package com.ethfoo.server;

import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerHeartBeatHandler extends	SimpleChannelInboundHandler<Request> {
	private int beatFailCount = 0;
	private static final int MAX_FAIL_COUNT = 5;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request msg)
			throws Exception {

		if(msg.isHeartBeat()){
			beatFailCount = 0;
			Response response = new Response();
			response.setHeartBeat(true);
			ctx.channel().writeAndFlush(response);
		}else{
			ctx.fireChannelRead(msg);
		}
		
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().toString() + " active");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println(ctx.channel().toString() + " inactive");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if( evt instanceof IdleStateEvent ){
			beatFailCount++;
			System.out.println("服务端空闲时间达到5s, beatFailCount=" + beatFailCount);
			if( beatFailCount > MAX_FAIL_COUNT ){
				System.out.println("准备关闭channel");
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

	
	
}
