package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.fastjson.JSON;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-17 11:24
 **/

public class MessageServerEncoder extends MessageToByteEncoder<MessageResponse> {
//    private Logger logger = LoggerFactory.getLogger(MessageServerEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessageResponse messageResponse, ByteBuf byteBuf) throws Exception {
        byte[] bytes = JSON.toJSONBytes(messageResponse);
        int dataLength = bytes.length;
        byteBuf.writeInt(dataLength);
        byteBuf.writeBytes(bytes);
    }
}
