package com.ethfoo.test;

import com.ethfoo.consumer.ConsumerProxy;
import com.ethfoo.registry.LocalAddressProvider;
import com.ethfoo.serializer.Response;

public class Consumer {

	public static void main(String[] args) {
		Hello hello = (Hello) new ConsumerProxy(new LocalAddressProvider()).bind(Hello.class);
		String response = hello.sayHello("fuck");
		System.out.println("Consumer receive: " + response);
		
	}

}
