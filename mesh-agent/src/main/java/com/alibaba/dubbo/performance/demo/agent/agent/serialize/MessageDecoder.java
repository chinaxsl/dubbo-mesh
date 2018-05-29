package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageFuture;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.sun.org.apache.regexp.internal.RE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @program: dubbo-mesh
 * @description: 消息解码接口
 * @author: XSL
 * @create: 2018-05-18 16:11
 **/

public class MessageDecoder extends ByteToMessageDecoder {
    private Logger logger = LoggerFactory.getLogger(MessageDecoder.class);
    private MessageCodeUtil codeUtil = null;
    public MessageDecoder(final MessageCodeUtil codeUtil) {
        this.codeUtil = codeUtil;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
//        logger.info("buffer " + byteBuf.capacity());
        //处理粘包 消息头长度不对直接返回
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
              if (response instanceof MessageResponse) {
                  MessageFuture future = Holder.removeRequest(((MessageResponse) response).getMessageId());
                  if (future!=null) {
                      future.done(response);
                  }
              }
              list.add(response);
          } catch (IOException e) {
              e.printStackTrace();
          }
        }
    }
}
