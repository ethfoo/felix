package com.ethfoo.serializer;

public class Request {
	
	private String requestId;
	private String className;
	private String methodName;
	private Class<?>[] parameterTypes;
	private Object[] parameters;
	private boolean heartBeat;
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}
	public boolean isHeartBeat() {
		return heartBeat;
	}
	public void setHeartBeat(boolean heartBeat) {
		this.heartBeat = heartBeat;
	}
	@Override
	public String toString() {
		return "[requestId: " + requestId + " className: " + className + " methodName: " + methodName + " ]";
	}
	
	

}
