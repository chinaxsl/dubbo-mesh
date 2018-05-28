package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/16.
 */



import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageFuture;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-16 20:29
 **/

public class NettyServerHandler extends SimpleChannelInboundHandler<MessageRequest> {
//    private Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
//    private ConcurrentHashMap<String,String> concurrentHashMap = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageRequest messageRequest) throws Exception {
//        concurrentHashMap.put(messageRequest.getMessageId(),Thread.currentThread().getName());
        MessageFuture future = InvokeService.executeInvoke(messageRequest);
        Runnable callable = () -> {
            try {
                Integer result = JSON.parseObject((byte[]) future.get(),Integer.class);
                MessageResponse response = new MessageResponse(messageRequest.getMessageId(),result);
//                logger.info("thread out " + concurrentHashMap.get(messageRequest.getMessageId()) + "thread run in " + Thread.currentThread().getName());
                channelHandlerContext.writeAndFlush(response,channelHandlerContext.voidPromise());
            } catch (Exception e) {
                channelHandlerContext.writeAndFlush(new MessageResponse(messageRequest.getMessageId(),"-1"));
                e.printStackTrace();
            }
        };
        future.addListener(callable,channelHandlerContext.channel().eventLoop());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
