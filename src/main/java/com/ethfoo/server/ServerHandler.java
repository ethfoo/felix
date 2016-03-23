package com.ethfoo.server;

import java.lang.reflect.Method;
import java.util.Map;

import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerHandler extends SimpleChannelInboundHandler<Request>{
	private Map<String, Object> exportClassMap;
	
	
	public ServerHandler(Map<String, Object> exportClassMap){
		this.exportClassMap = exportClassMap;
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request request)
			throws Exception {
		Response response = new Response();
		response.setRequestId(request.getRequestId());
		try{
			Object result = handle(request);
			System.out.println("serverHandler: result-->" + result.toString());
			response.setResult(result);
		}catch(Throwable t){
			System.out.println("serverHandler: throwable-->" + t.getMessage());
			response.setError(t);
		}
		
		ctx.writeAndFlush(response);
		
	}
	
	private Object handle(Request request) throws Throwable{
		String clazzName = request.getClassName();
		System.out.println("provider reveive: " + clazzName);
		Object serviceBean = exportClassMap.get(clazzName);
		
		Class<?> serviceClass = serviceBean.getClass();
		String methodName = request.getMethodName();
		Class<?>[] parametersTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();
		
		/*
		 * jdk反射，对性能有影响, TODO:加入其他高效的替代
		 */
		Method method = serviceClass.getMethod(methodName, parametersTypes);
		method.setAccessible(true);
		return method.invoke(serviceBean, parameters);
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		// TODO Auto-generated method stub
		super.exceptionCaught(ctx, cause);
	}
	

}
