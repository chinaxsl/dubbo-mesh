package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/27.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageFuture;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-27 12:59
 **/

public class MessageClientEncoder extends MessageToByteEncoder<Object> {
    private Logger logger = LoggerFactory.getLogger(MessageClientEncoder.class);
    public MessageClientEncoder(MessageCodeUtil codeUtil) {
        this.codeUtil = codeUtil;
    }
    private MessageCodeUtil codeUtil = null;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object object, ByteBuf byteBuf) throws Exception {
        logger.info(byteBuf.isDirect() + "");
        codeUtil.encode(byteBuf,object);
//        if (object instanceof MessageRequest) {
//            logger.info("request put");
//            Holder.putFuture(((MessageRequest)object).getMessageId(),new MessageFuture<MessageResponse>());
//        }
    }
}
