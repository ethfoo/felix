package com.ethfoo.registry;

public class LocalAddressProvider implements AddressProvider{
	private String host;
	private int port;
	
	

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	

}
