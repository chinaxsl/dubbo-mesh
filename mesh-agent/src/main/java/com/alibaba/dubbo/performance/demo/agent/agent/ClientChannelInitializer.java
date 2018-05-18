package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;


/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-17 00:03
 **/

public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    final public static int MESSAGE_LENGTH = 4;
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new MessageClientEncoder())
                .addLast(new MessageClientDecoder())
                .addLast(new NettyClientHandler());
    }
}
