package com.ethfoo.test;

import com.ethfoo.annotation.RpcProvider;

@RpcProvider(Hello.class)
public class HelloImple implements Hello{

	public String sayHello(String strAdd) {
		return "hello" + strAdd;
	}

}
