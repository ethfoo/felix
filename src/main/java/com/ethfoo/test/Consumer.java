package com.ethfoo.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ethfoo.consumer.ConsumerProxy;
import com.ethfoo.consumer.RpcFuture;
import com.ethfoo.consumer.RpcFutureListener;
import com.ethfoo.registry.LocalAddressProvider;
import com.ethfoo.serializer.Response;

public class Consumer {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:clientApplicationContext.xml");
		ConsumerProxy proxy = (ConsumerProxy)ctx.getBean("ConsumerProxy");
		proxy.bind(Hello.class);
		/*try {
			String result = hello.sayHello("fuck");
			System.out.println("Consumer receive: " + result);
		} catch (Throwable e) {
			e.printStackTrace();
		}*/
		
		
		try {
			RpcFuture future = proxy.call("sayHello", "fuckU");
			future.addListener(new RpcFutureListener() {
				
				@Override
				public void onResult(Object result) {
					System.out.println("Consumer receive result: " + result);
				}
				
				@Override
				public void onException(Throwable throwable) {
					System.out.println("Consumer receive throwable: " + throwable);
				}
			});
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
	}

}
