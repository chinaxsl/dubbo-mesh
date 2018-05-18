package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/16.
 */

import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IpHelper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-16 20:18
 **/

public class NettyTcpServer {
    private Logger logger = LoggerFactory.getLogger(NettyTcpServer.class);
//    private EtcdRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
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
                    .childHandler(new NettyChannelHandler());
            logger.info("退出3");
            ChannelFuture channelFuture = serverBootstrap.bind("0.0.0.0",port).sync();
            logger.info("退出2");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
          e.printStackTrace();
        } finally {
            logger.info("退出1");
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
        logger.info("退出");
    }

    private class NettyChannelHandler extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel SocketChannel) throws Exception {
            SocketChannel.pipeline().addLast(new MessageServerEncoder())
                    .addLast(new MessageServerDecoder())
                    .addLast(new NettyServerHandler());
//            logger.info("intial");
        }
    }

//    public static void main(String[] args) throws Exception {
//        new NettyTcpServer().bind(8080);
//    }
}
