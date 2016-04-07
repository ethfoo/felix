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
 * 
 * @author ethfoo
 *
 */
public class ClientTaskRunnable implements Runnable {
	private CountDownLatch latch;
	private ConsumerProxy proxy;
	private String content;
	private int loopNum;

	// private Map<String, Integer> results = new HashMap<>();

	public ClientTaskRunnable(ConsumerProxy proxy, CountDownLatch latch,
			int loopNum) {
		this.latch = latch;
		this.proxy = proxy;
		this.loopNum = loopNum;
		// content = new byte[1024].toString();
		content = "Hello";
	}

	@Override
	public void run() {

		Hello hello = proxy.bind(Hello.class);
		for (int i = 0; i < loopNum; i++) {
			try {
				RpcFuture future = proxy.call("sayHello", content);
				/*
				 * future.addListener(new RpcFutureListener() {
				 * 
				 * @Override public void onResult(Object result) {
				 * latch.countDown(); }
				 * 
				 * @Override public void onException(Throwable throwable) {
				 * throwable.printStackTrace(); } });
				 */
				Object obj = future.get();
				if (obj instanceof String) {
					String result = (String) obj;
					// System.out.println(Thread.currentThread().getName() +
					// " receive :" + result);
				} else if (obj instanceof Throwable) {
					Throwable th = (Throwable) obj;
					// System.out.println(Thread.currentThread().getName() +
					// " throwable :" + th.getMessage());
				}
				// String result = hello.sayHello(content);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		latch.countDown();
		/*
		 * System.out.println(Thread.currentThread().getName() + " countDown");
		 */}

}
