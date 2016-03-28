package com.ethfoo.test;

import java.util.ArrayList;
import java.util.List;

import com.ethfoo.annotation.RpcProvider;

@RpcProvider(Hello.class)
public class HelloImple implements Hello{

	public String sayHello(String strAdd) {
		return strAdd;
	}

	@Override
	public List<String> getPersons() {
		List<String> list = new ArrayList<>();
		list.add("felix");
		list.add("lena");
		list.add("ethfoo");
		return list;
	}

}
