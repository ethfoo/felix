package com.ethfoo.consumer;

import java.util.concurrent.ConcurrentMap;

import com.ethfoo.serializer.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConsumerClientHandler extends SimpleChannelInboundHandler<Response>{
	private ConcurrentMap<String, RpcFuture> rpcFutureMap;

	public ConsumerClientHandler(ConcurrentMap<String, RpcFuture> rpcFutureMap){
		this.rpcFutureMap = rpcFutureMap;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Response response)
			throws Exception {
		
		handlerResponse(response);
	}

	private void handlerResponse(Response response) {
		String requestId = response.getRequestId();
		RpcFuture future = rpcFutureMap.remove(requestId);
		if( response.isError() ){
			future.setThrowable(response.getError());
		}else{
			future.setResult(response.getResult());
		}
	}

}
