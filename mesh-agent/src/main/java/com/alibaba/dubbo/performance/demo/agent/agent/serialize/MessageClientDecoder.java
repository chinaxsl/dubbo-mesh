package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/27.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageFuture;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-27 13:04
 **/

public class MessageClientDecoder extends ByteToMessageDecoder {
    private Logger logger = LoggerFactory.getLogger(MessageClientDecoder.class);
    public MessageClientDecoder(MessageCodeUtil codeUtil) {
        this.codeUtil = codeUtil;
    }

    private MessageCodeUtil codeUtil = null;
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < MessageCodeUtil.HEADER_LENGTH) {
            return;
        }
        byteBuf.markReaderIndex();
        int messageLength = byteBuf.readInt();
        if (messageLength < 0) {
            channelHandlerContext.close();
        }
        if (byteBuf.readableBytes() < messageLength) {
            byteBuf.resetReaderIndex();
            return;
        } else {
            byte[] messageBody = new byte[messageLength];
            byteBuf.readBytes(messageBody);
            try {
                Object response = codeUtil.decode(messageBody);
                logger.info(byteBuf.isDirect() + "");
                MessageFuture future = Holder.removeRequest(((MessageResponse) response).getMessageId());
                if (future!=null) {
                    future.done(response);
                }
                list.add(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
