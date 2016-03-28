package com.ethfoo.benchmark;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import com.ethfoo.async.RpcFuture;
import com.ethfoo.async.RpcFutureListener;
import com.ethfoo.consumer.ConsumerProxy;
import com.ethfoo.test.Hello;

/**
 * 每个测试的Runnable线程
 * @author ethfoo
 *
 */
public class ClientTaskRunnable implements Runnable{
	private CountDownLatch latch;
	private ConsumerProxy proxy;
	private String content;
	
	//private Map<String, Integer> results = new HashMap<>();
	
	
	public ClientTaskRunnable(ConsumerProxy proxy, CountDownLatch latch){
		this.latch = latch;
		this.proxy = proxy;
		content = new byte[1024].toString();
	}
	

	@Override
	public void run() {
		
		Hello hello = proxy.bind(Hello.class);
		for( int i=0; i<10000; i++){
			try {
				//RpcFuture future = proxy.call("sayHello", content);
				//future.get();
				hello.sayHello(content);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		
		latch.countDown();
	}

}
