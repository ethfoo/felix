package com.ethfoo.registry;

public class LocalAddressProvider implements AddressProvider{

	public String getHost() {
		return "127.0.0.1";
	}

	public int getPort() {
		return 3456;
	}
	
	

}
