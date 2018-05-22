package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.or.jms.MessageRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-18 16:08
 **/

public class MessageEncoder extends MessageToByteEncoder<Object> {
    private Logger logger = LoggerFactory.getLogger(MessageEncoder.class);
    private MessageCodeUtil codeUtil = null;

    public MessageEncoder(final MessageCodeUtil codeUtil) {
        this.codeUtil = codeUtil;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        codeUtil.encode(byteBuf,object);
    }
}
