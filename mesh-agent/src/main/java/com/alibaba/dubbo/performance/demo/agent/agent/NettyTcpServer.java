package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/16.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.serialize.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-16 20:18
 **/

public class NettyTcpServer {
    int parallel = Runtime.getRuntime().availableProcessors() * 2;
    public void bind(int port) throws Exception {
        WaitService.init();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(parallel);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new NettyServerInitializer());
            ChannelFuture channelFuture = serverBootstrap.bind("0.0.0.0",port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private class NettyServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel SocketChannel) throws Exception {
            KryoCodeUtil util = new KryoCodeUtil(KryoPoolFactory.getKryoPoolInstance());
            SocketChannel.pipeline()
                    .addLast(new KryoEncoder(util))
                    .addLast(new KryoDecoder(util))
                    .addLast(new NettyServerHandler());
//            SocketChannel.pipeline()
//                    .addLast(new JsonEncoder(JsonCodeUtil.getRequestCodeUtil()))
//                    .addLast(new JsonDecoder(JsonCodeUtil.getRequestCodeUtil()))
//                    .addLast(new NettyServerHandler());
        }
    }

}
