package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.dubbo.ConnecManager;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClientInitializer;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-17 16:36
 **/

public class MyConnectManager {
//    private ConcurrentHashMap<Endpoint,Channel> channelMap = new ConcurrentHashMap<>();
    private HashMap<Endpoint,Channel> channelMap = new HashMap<>();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(8);

    private Bootstrap bootstrap;
    private Object lock = new Object();


    public Channel getChannel(String url,int port) throws Exception {
        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }
        Endpoint endpoint = new Endpoint(url,port);
        if (channelMap.containsKey(endpoint)) {
            return channelMap.get(endpoint);
        }
        Channel channel = bootstrap.connect(url, port).sync().channel();
        return channel;
    }

    public void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, UnpooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer());
    }
    public Channel getChannel(Endpoint endpoint) throws Exception {
        if (!channelMap.containsKey(endpoint)) {
            Channel channel = getChannel(endpoint.getHost(),endpoint.getPort());
            synchronized (channelMap) {
                if (!channelMap.containsKey(endpoint)) {
                    channelMap.put(endpoint, channel);
                }
            }
        }
        return channelMap.get(endpoint);
    }
}
