package com.ethfoo.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ethfoo.async.RpcFuture;
import com.ethfoo.async.RpcFutureListener;
import com.ethfoo.consumer.ConsumerProxy;
import com.ethfoo.registry.LocalAddressProvider;
import com.ethfoo.serializer.Response;

public class Consumer {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:clientApplicationContext.xml");
		ConsumerProxy proxy = (ConsumerProxy)ctx.getBean("ConsumerProxy");
		
		/*try {
			Hello hello = proxy.bind(Hello.class);
			String result = hello.sayHello("fuck");
			System.out.println("Consumer receive: " + result);
			//List<String> list = hello.getPersons();
			List<String> list = new ArrayList<>();
			list = hello.getPersons();
			Iterator<String> it = list.iterator();
			while(it.hasNext()){
				System.out.println(it.next());
			}
		
		} catch (Throwable e) {
			e.printStackTrace();
		}*/
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		try {
			proxy.bind(Hello.class);
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
			e.printStackTrace();
		}
		
		
	
		
	}

}
