package com.ethfoo.consumer;

import java.util.concurrent.ConcurrentMap;

import com.ethfoo.aop.RpcInvokeHook;
import com.ethfoo.async.RpcFuture;
import com.ethfoo.serializer.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ConsumerClientHandler extends SimpleChannelInboundHandler<Response>{
	private ConcurrentMap<String, RpcFuture> rpcFutureMap;
	private RpcInvokeHook hook;

	public ConsumerClientHandler(ConcurrentMap<String, RpcFuture> rpcFutureMap){
		this.rpcFutureMap = rpcFutureMap;
	}
	public ConsumerClientHandler(ConcurrentMap<String, RpcFuture> rpcFutureMap, RpcInvokeHook hook){
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
		if(hook!=null){
			hook.afterInvoke(response);
		}
		if( response.isError() ){
			future.setThrowable(response.getError());
		}else{
			future.setResult(response.getResult());
		}
	}

}
