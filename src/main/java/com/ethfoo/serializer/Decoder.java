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
		   if (in.readableBytes() < 4) {
	            return;
	        }
	        in.markReaderIndex();
	        int dataLength = in.readInt();
	        if (dataLength < 0) {
	            ctx.close();
	        }
	        if (in.readableBytes() < dataLength) {
	            in.resetReaderIndex();
	            return;
	        }
	        byte[] data = new byte[dataLength];
	        in.readBytes(data);

	        Object obj = ProtostuffSer.deserialize(data, objClazz);
	        out.add(obj);
	}

}
