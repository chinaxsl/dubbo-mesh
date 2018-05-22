package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.serialize.*;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-17 16:36
 **/

public class MyConnectManager {
    private Logger logger = LoggerFactory.getLogger(MyConnectManager.class);
    private HashMap<Endpoint,Channel> channelMap = new HashMap<>();
    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    private List<Channel> channelList;
    private Bootstrap bootstrap;
    private Object lock = new Object();
    private Object listLock = new Object();
    private Random random = new Random();
    private static Map<String,Integer>endsMap = new HashMap<>();
    static {
        endsMap.put("10.10.10.3",2);
        endsMap.put("10.10.10.4",3);
        endsMap.put("10.10.10.5",4);

    }

    public Channel getChannel(String url,int port) throws Exception {
        if (null == bootstrap) {
            synchronized (lock) {
                if (null == bootstrap) {
                    initBootstrap();
                }
            }
        }
        Channel channel = bootstrap.connect(url, port).sync().channel();
        return channel;
    }

    public void initBootstrap() {
        bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,500)
                .channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
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

    public Channel getRandomChannel(List<Endpoint> endpoints) throws Exception {
        if (channelList == null) {
            List<Channel> tempList = new ArrayList<>();
            for (Endpoint endpoint : endpoints) {
                for (int i = 0; i < endsMap.get(endpoint.getHost()); i++) {
                    Channel channel = getChannel(endpoint.getHost(), endpoint.getPort());
                    tempList.add(channel);
                }
                logger.info("tempSize = " + tempList.size() + "");
            }
            synchronized (listLock) {
                if (channelList == null) {
                    channelList = new ArrayList<>(tempList);
                } else {
                    for (Channel endpoint:tempList) {
                        endpoint.close();
                    }
                }
            }
        }
        return channelList.get(random.nextInt(channelList.size()));
    }

    /**
     * @program: TcpProject
     * @description:
     * @author: XSL
     * @create: 2018-05-17 00:03
     **/

    private static class NettyClientInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            KryoCodeUtil util = new KryoCodeUtil(KryoPoolFactory.getKryoPoolInstance());
            socketChannel.pipeline()
                    .addLast(new KryoEncoder(util))
                    .addLast(new KryoDecoder(util))
                    .addLast(new NettyClientHandler());
        }
    }
}
