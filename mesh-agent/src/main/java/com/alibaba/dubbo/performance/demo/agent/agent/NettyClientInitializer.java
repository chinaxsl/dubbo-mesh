package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/27.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.serialize.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-27 21:49
 **/

public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        KryoCodeUtil util = KryoCodeUtil.getKryoCodeUtil();
        socketChannel.pipeline()
                .addLast(new KryoEncoder(util))
                .addLast(new KryoDecoder(util));
//                ProtostuffCodeUtil util = ProtostuffCodeUtil.getClientCodeUtil();
//                socketChannel.pipeline().addLast(new ProtostuffEncoder(util))
//                        .addLast(new ProtostuffDecoder(util))
//                        .addLast(new NettyClientHandler());
    }
}