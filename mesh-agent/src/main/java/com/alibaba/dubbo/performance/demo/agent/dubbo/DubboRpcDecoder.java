package com.alibaba.dubbo.performance.demo.agent.dubbo;

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.Bytes;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcRequestHolder;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcResponse;
import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.Arrays;
import java.util.List;
public class DubboRpcDecoder extends ByteToMessageDecoder {
    // header length.
    protected static final int HEADER_LENGTH = 16;
    private byte[] header = new byte[HEADER_LENGTH - 4];
    protected static final byte FLAG_EVENT = (byte) 0x20;
    RpcResponse response = new RpcResponse();
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {

        try {
            do {
                int savedReaderIndex = byteBuf.readerIndex();
                Object msg = null;
                try {
                    msg = decode2(byteBuf);
                } catch (Exception e) {
                    throw e;
                }
                if (msg == DecodeResult.NEED_MORE_INPUT) {
                    byteBuf.readerIndex(savedReaderIndex);
                    break;
                }

                list.add(msg);
            } while (byteBuf.isReadable());
        } finally {
            if (byteBuf.isReadable()) {
                byteBuf.discardReadBytes();
            }
        }


        //list.add(decode2(byteBuf));
    }

    enum DecodeResult {
        NEED_MORE_INPUT, SKIP_INPUT
    }

    /**
     * Demo为简单起见，直接从特定字节位开始读取了的返回值，demo未做：
     * 1. 请求头判断
     * 2. 返回值类型判断
     *
     * @param byteBuf
     * @return
     */
    private Object decode2(ByteBuf byteBuf){

//        int savedReaderIndex = byteBuf.readerIndex();
        int readable = byteBuf.readableBytes();

        if (readable < HEADER_LENGTH) {
            return DecodeResult.NEED_MORE_INPUT;
        }

        int status = byteBuf.readInt();
        long requestId = byteBuf.readLong();
        int len = byteBuf.readInt();
        int tt = len + HEADER_LENGTH;
        if (readable < tt) {
            return DecodeResult.NEED_MORE_INPUT;
        }
//        ByteBuf dataBuf = byteBuf.retainedSlice(byteBuf.readerIndex() +1,len - 2);
        byte[] dataBytes = new byte[len-2];
        byteBuf.skipBytes(1);
        byteBuf.readBytes(dataBytes);
        byteBuf.skipBytes(1);

        String id = String.valueOf(requestId);
        response.setRequestId(id);
        response.setBytes(dataBytes);
        MessageFuture future = RpcRequestHolder.remove(response.getRequestId());
        if (future != null) {
            future.done(response.getBytes());
        }
        return response;
    }
}
