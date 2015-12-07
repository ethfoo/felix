package com.ethfoo.consumer;

import com.ethfoo.serializer.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConsumerClientHandler extends SimpleChannelInboundHandler<Response>{

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response response)
			throws Exception {
		
		
	}

}
