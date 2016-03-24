package com.ethfoo.consumer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

import com.ethfoo.registry.AddressProvider;
import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public class ConsumerProxy implements InvocationHandler{

	private AddressProvider addressProvider;
	
	public ConsumerProxy(AddressProvider addressProvider){
		this.addressProvider = addressProvider;
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
	 * 调用方法，返回远程服务器返回的Response
	 */
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		
		//封装request
		Request request = new Request();
		request.setRequestId(UUID.randomUUID().toString());
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setParameters(args);
		request.setParameterTypes(method.getParameterTypes());
		
		String host = addressProvider.getHost();
		int port = addressProvider.getPort();
		
		//发送request
		ConsumerClient client = new ConsumerClient(host, port);
		Response response = client.init(request);
		
		if(response.isError()){
			throw response.getError();
			//return null;
		}else{
			return response.getResult();
		}
		
	}

	
}
