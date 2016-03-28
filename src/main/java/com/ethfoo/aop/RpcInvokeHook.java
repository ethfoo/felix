package com.ethfoo.aop;

import com.ethfoo.serializer.Request;
import com.ethfoo.serializer.Response;

public interface RpcInvokeHook {
	
	void beforeInvoke(Request request);
	void afterInvoke(Response response);

}
