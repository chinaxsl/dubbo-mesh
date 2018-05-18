package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

import com.sun.org.apache.regexp.internal.RE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.util.List;

/**
 * @program: dubbo-mesh
 * @description: 消息解码接口
 * @author: XSL
 * @create: 2018-05-18 16:11
 **/

public class MessageDecoder extends ByteToMessageDecoder {
    private MessageCodeUtil codeUtil = null;
    public MessageDecoder(final MessageCodeUtil codeUtil) {
        this.codeUtil = codeUtil;
    }
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
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
              list.add(response);
          } catch (IOException e) {
              e.printStackTrace();
          }
        }
    }
}
