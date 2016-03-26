package com.ethfoo.consumer;

public interface RpcFutureListener {
	
	void onResult(Object result);
	void onException(Throwable throwable);
}
