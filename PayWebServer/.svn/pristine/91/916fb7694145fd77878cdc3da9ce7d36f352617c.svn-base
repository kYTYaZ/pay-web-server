package com.huateng.pay.handler.netty;

import java.nio.ByteOrder;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class NettyLengthFieldBasedFrameDecoder extends LengthFieldBasedFrameDecoder{

	
	public NettyLengthFieldBasedFrameDecoder(int maxFrameLength, int lengthFieldOffset,int lengthFieldLength, int lengthAdjustment,int initialBytesToStrip) {
			super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment,initialBytesToStrip);
	}

	
	@Override
	public long getUnadjustedFrameLength(ByteBuf buf, int offset, int length, ByteOrder order) {

		int readerIndex = buf.readerIndex();
	    int writerIndex = buf.writerIndex();
	    buf.resetReaderIndex();
	     
	    byte [] bt = new byte[6];
	    buf.readBytes(bt);

	    long frameLength =  Long.valueOf(new String(bt));
	   	buf.setIndex(readerIndex, writerIndex);
	
	    return frameLength;
	}	
}
 