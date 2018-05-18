package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-17 11:06
 **/

public class MessageClientEncoder extends MessageToByteEncoder<MessageRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageRequest messageRequest, ByteBuf byteBuf) throws Exception {
        byte[] bytes = JSON.toJSONBytes(messageRequest);
        int dataLength = bytes.length;
        byteBuf.writeInt(dataLength);
        byteBuf.writeBytes(bytes);
    }
}
