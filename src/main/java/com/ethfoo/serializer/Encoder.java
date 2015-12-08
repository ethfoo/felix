package com.ethfoo.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class Encoder extends MessageToByteEncoder{
	private Class<?> objClazz;
	
	public Encoder(Class<?> requestClazz){
		this.objClazz = requestClazz;
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		if( objClazz.isInstance(msg)){
			byte[] data = ProtostuffSer.serialize(msg);
			out.writeInt(data.length);
			out.writeBytes(data);
		}
	}

}
