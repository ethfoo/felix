package com.ethfoo.serializer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class ProtostuffSer {
	private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<Class<?>, Schema<?>>();
	private static Objenesis objenesis = new ObjenesisStd(true);
	
	private ProtostuffSer(){ }
	

	/*
	 * 缓存//TODO
	 */
	private static <T> Schema<T> getSchema(Class<T> cls){
		Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
		if(schema == null){
			schema = RuntimeSchema.createFrom(cls);
			if(schema != null){
				cachedSchema.put(cls, schema);
			}
		}
		return schema;
	}

	
	/*
	 * Serialize
	 */
	public static <T> byte[] serialize(T obj){
		Class<T> cls = (Class<T>)obj.getClass();
		LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
		try{
			Schema<T> schema = getSchema(cls);
			return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			buffer.clear();
		}

		return null;//TODO 
	}
	
	/*
	 * Deserialize
	 */
	public static <T> T deserialize(byte[] data, Class<?> cls){
		try{
			T message = (T)objenesis.newInstance(cls);
			Schema<T> schema = (Schema<T>) getSchema(cls);
			ProtostuffIOUtil.mergeFrom(data, message, schema);
			return message;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;//TODO
	}
	
}













