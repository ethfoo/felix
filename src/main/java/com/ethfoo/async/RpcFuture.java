package com.ethfoo.async;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class RpcFuture {

	private Object result;
	private Throwable throwable;
	private CountDownLatch countDownLatch; 
	private RpcFutureListener listener;
	private enum state{UNCOMPLETE, SUCCESS, EXCEPTION};
	private static state currentState;

	public RpcFuture(){
		countDownLatch = new CountDownLatch(1);
		currentState = state.UNCOMPLETE;
	}

	
	/*
	 * 阻塞得到结果
	 */
	public Object get() throws Throwable{
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if( currentState.equals(state.SUCCESS)){
			return result;
		}
		else if( currentState.equals(state.EXCEPTION)){
			throw throwable;
		}
		else {
			throw new RuntimeException("RpcFuture Exception");//RpcFuture本身的异常
		}
		 
	}
	
	/*
	 * 带超时的get,超时时间单位为毫秒Milliseconds
	 */
	public Object get(long timeout) throws Throwable{
		try {
			countDownLatch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//超时后可能异步任务尚未完成
		if( currentState.equals(state.UNCOMPLETE)){
			throw new RuntimeException("timeout and uncomplete Exception");//TODO 改为自定义exception
		}
		else if( currentState.equals(state.SUCCESS)){
			return result;
		}
		else if( currentState.equals(state.EXCEPTION)){
			throw throwable;
		}
		else{
			throw new RuntimeException("RpcFuture Exception");
		}
		
	}
	
	/*
	 * 当接受到返回的结果时设置，通知listener
	 */
	public void setResult(Object result){
		this.result = result;
		currentState = state.SUCCESS;
		if( listener != null){
			listener.onResult(result);
		}
		countDownLatch.countDown();
	}
	
	/*
	 * 当接受到的返回为异常时，通知listener
	 */
	public void setThrowable(Throwable throwable){
		this.throwable = throwable;
		currentState = state.EXCEPTION;
		if( listener != null){
			listener.onException(throwable);
		}
		countDownLatch.countDown();
	}
	
	/*
	 * 添加listener
	 */
	public void addListener(RpcFutureListener listener){
		this.listener = listener; 
	}
	
	/*
	 * 判断是异步任务是否完成
	 */
	public boolean isDone(){
		if( currentState != state.UNCOMPLETE ){
			return true;
		}
		return false;
	}

}
