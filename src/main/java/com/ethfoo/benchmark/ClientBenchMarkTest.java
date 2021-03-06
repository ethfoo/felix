package com.ethfoo.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ethfoo.consumer.ConsumerProxy;

public class ClientBenchMarkTest {
	private int threadNum;
	private int loopNum;
	private CountDownLatch latch;
	private ConsumerProxy proxy;
	
	private Long startTime;
	private Long endTime;
	
	public ClientBenchMarkTest(int threadNum, int loopNum){
		latch = new CountDownLatch(threadNum);
		this.threadNum = threadNum;
		this.loopNum = loopNum;
		ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:clientApplicationContext.xml");
		proxy = (ConsumerProxy)ctx.getBean("ConsumerProxy");
	}
	
	public void runTest(){
		try {
			Thread.sleep(3000); //delay for the channel connection
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("threadNum-->" + threadNum);
		System.out.println("loopNum-->" + loopNum);
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
		startTime = System.currentTimeMillis();
		System.out.println("startTime=" + startTime);
		for(int i=0; i<threadNum; i++){
			executor.submit(new ClientTaskRunnable(proxy, latch,loopNum));
			//Thread thread = new Thread(new ClientTaskRunnable(proxy, latch, loopNum), "thread-"+i);
			//thread.start();
		}
		
		printInfo();
	}
	
	
	private void printInfo(){
		try {
			latch.await();
			endTime = System.currentTimeMillis();
			System.out.println("endTime=" + endTime);
			double time = (endTime - startTime)/1000f;
			double tps = ((threadNum*loopNum)/time);
			
			System.out.println("use time: " + time);
			System.out.println("tps-->" + tps);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		new ClientBenchMarkTest(10, 30000).runTest();
	}

}
