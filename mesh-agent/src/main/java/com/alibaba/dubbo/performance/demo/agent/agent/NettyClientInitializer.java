package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/27.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.serialize.KryoClientDecoder;
import com.alibaba.dubbo.performance.demo.agent.agent.serialize.KryoClientEncoder;
import com.alibaba.dubbo.performance.demo.agent.agent.serialize.KryoCodeUtil;
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
                .addLast(new KryoClientEncoder(util))
                .addLast(new KryoClientDecoder(util));
//                ProtostuffCodeUtil util = ProtostuffCodeUtil.getClientCodeUtil();
//                socketChannel.pipeline().addLast(new ProtostuffEncoder(util))
//                        .addLast(new ProtostuffDecoder(util))
//                        .addLast(new NettyClientHandler());
    }
}