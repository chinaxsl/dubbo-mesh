package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-17 11:24
 **/

public class MessageServerDecoder extends ByteToMessageDecoder {
    protected static final int HEADER_LENGTH = 4;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < HEADER_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (dataLength < 0) {
            channelHandlerContext.close();
        }
        if (byteBuf.readableBytes() < dataLength){
            byteBuf.resetReaderIndex();
            return;
        }
        byte[] bytes = new byte[dataLength];
        byteBuf.readBytes(bytes);
        Object response = JSON.parseObject(bytes,MessageRequest.class);
        list.add(response);
    }
}
