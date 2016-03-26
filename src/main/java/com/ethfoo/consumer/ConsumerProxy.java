package com.ethfoo.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.ethfoo.registry.AddressProvider;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ConsumerProxy implements InvocationHandler{

	
	private ConsumerClient consumerClient;
	
	public ConsumerProxy(){
		
	}
	
	public void setConsumerClient(ConsumerClient consumerClient) {
		this.consumerClient = consumerClient;
	}

	/*
	 * 绑定委托对象并返回一个代理类
	 */
	@SuppressWarnings("unchecked")
	public <T> T bind(Class<?> interfaces){
		return (T)Proxy.newProxyInstance(interfaces.getClassLoader(), 
				new Class<?>[]{interfaces}, this);
	}

	/*
	 * 调用方法，返回异步调用的RpcFuture
	 */
	public RpcFuture call(Method method, Object[] args){
		//封装request
		Request request = new Request();
		request.setRequestId(UUID.randomUUID().toString());
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setParameters(args);
		request.setParameterTypes(method.getParameterTypes());
		
		RpcFuture future = consumerClient.send(request);
		
		return future;
	}
	
	
	/*
	 * 返回同步调用的结果，其实也是用的异步调用的阻塞方法
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
		
		RpcFuture future = call(method, args);
	
		return future.get();
	}

	
}
