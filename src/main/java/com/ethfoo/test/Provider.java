package com.ethfoo.test;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.ethfoo.registry.LocalAddressProvider;
import com.ethfoo.server.Server;

public class Provider {

	public static void main(String[] args) {
		
		new ClassPathXmlApplicationContext("classpath:serverApplicationContext.xml");
		//new FileSystemXmlApplicationContext("/felix/src/serverApplicationContext.xml");
	}

}
