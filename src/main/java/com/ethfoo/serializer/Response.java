package com.ethfoo.serializer;

public class Response {

	private String requestId;
	private Throwable error;
	private Object result;
	private boolean heartBeat;
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public Throwable getError() {
		return error;
	}
	public void setError(Throwable error) {
		this.error = error;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	
	public boolean isError(){
		return error != null;
	}
	public boolean isHeartBeat() {
		return heartBeat;
	}
	public void setHeartBeat(boolean heartBeat) {
		this.heartBeat = heartBeat;
	}
	@Override
	public String toString() {
		return "[requestId: " + requestId +" error: " + error + " result: " + result + "]"; 
	}
	
	
}
