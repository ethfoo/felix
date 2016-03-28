package com.ethfoo.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ethfoo.consumer.ConsumerProxy;

public class ClientBenchMarkTest {
	private int threadNum;
	private CountDownLatch latch;
	private ConsumerProxy proxy;
	
	private Long startTime;
	private Long endTime;
	
	public ClientBenchMarkTest(int threadNum){
		latch = new CountDownLatch(threadNum);

		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:clientApplicationContext.xml");
		proxy = (ConsumerProxy)ctx.getBean("ConsumerProxy");

	}
	
	public void runTest(){
		
		//ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		//executor.submit(new ClientTaskRunnable(proxy, latch));
		startTime = System.currentTimeMillis();
		for(int i=0; i<threadNum; i++){
			Thread thread = new Thread(new ClientTaskRunnable(proxy, latch), "thread-"+i);
			thread.start();
		}
		
		printInfo();
	}
	
	
	private void printInfo(){
		try {
			latch.await();
			endTime = System.currentTimeMillis();
			Long tps = (threadNum*10000)/(endTime-startTime);
			System.out.println("tps-->" + tps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
