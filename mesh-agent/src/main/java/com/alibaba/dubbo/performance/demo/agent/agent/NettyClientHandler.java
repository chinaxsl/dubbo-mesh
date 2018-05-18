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

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-17 00:12
 **/

public class NettyClientHandler extends SimpleChannelInboundHandler<MessageResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageResponse messageResponse) throws Exception {
        MyFuture<MessageResponse> future = Holder.removeRequest(messageResponse.getMessageId());
        future.done(messageResponse);
    }
}
