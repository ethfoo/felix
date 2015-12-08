package com.ethfoo.serializer;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class Decoder extends ByteToMessageDecoder{
	private Class<?> objClazz;
	
	public Decoder(Class<?> objClazz){
		this.objClazz = objClazz;
	}
	
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		
	}

}
