package com.ethfoo.async;

public interface RpcFutureListener {
	
	void onResult(Object result);
	void onException(Throwable throwable);
}
