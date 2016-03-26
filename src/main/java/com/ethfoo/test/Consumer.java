package com.ethfoo.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ethfoo.consumer.ConsumerProxy;
import com.ethfoo.consumer.RpcFuture;
import com.ethfoo.registry.LocalAddressProvider;
import com.ethfoo.serializer.Response;

public class Consumer {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:clientApplicationContext.xml");
		ConsumerProxy proxy = (ConsumerProxy)ctx.getBean("ConsumerProxy");
		Hello hello = proxy.bind(Hello.class);
		try {
			String result = hello.sayHello("fuck");
			System.out.println("Consumer receive: " + result);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	
		
	}

}
