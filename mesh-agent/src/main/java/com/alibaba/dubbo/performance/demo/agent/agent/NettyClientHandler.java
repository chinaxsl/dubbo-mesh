package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-17 00:12
 **/

public class NettyClientHandler extends SimpleChannelInboundHandler<MessageResponse> {
//    private static ThreadLocal<HashMap<String,MyFuture>> futures;
//
//    static {
//        futures.set(futures.get());
//    }
//    private Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponse messageResponse) throws Exception {
//        logger.info("channelId " + channelHandlerContext.channel().id());
//        logger.info("threadId" + Thread.currentThread().getName());
        MyFuture<MessageResponse> future = Holder.removeRequest(messageResponse.getMessageId());
        if (future != null) {
            future.done(messageResponse);
        }
    }
}
