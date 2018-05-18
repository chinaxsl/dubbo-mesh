package com.alibaba.dubbo.performance.demo.agent.agent.serialize;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * Created by msi- on 2018/5/18.
 */
public interface MessageCodeUtil {
    //RPC消息报文头部
    public final static int HEADER_LENGTH = 4;
    public void encode(final ByteBuf out,final Object message) throws IOException;
    public Object decode(byte[] body) throws IOException;
}
