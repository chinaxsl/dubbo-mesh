package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/16.
 */



import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-16 20:29
 **/

public class NettyServerHandler extends SimpleChannelInboundHandler<MessageRequest> {
//    private Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageRequest messageRequest) throws Exception {
//        channelHandlerContext.writeAndFlush(new MessageResponse(messageRequest.getMessageId(),messageRequest.getParameter()));
//        logger.info(messageRequest.toString());
        RpcFuture future = WaitService.executeInvoke(messageRequest);
        Runnable callable = () -> {
            try {
                Integer result = JSON.parseObject((byte[]) future.get(),Integer.class);
                MessageResponse response = new MessageResponse(messageRequest.getMessageId(),result);
                channelHandlerContext.writeAndFlush(response);
            } catch (Exception e) {
                channelHandlerContext.writeAndFlush(new MessageResponse(messageRequest.getMessageId(),"-1"));
                e.printStackTrace();
            }
        };
        WaitService.execute(callable);
    }
}
