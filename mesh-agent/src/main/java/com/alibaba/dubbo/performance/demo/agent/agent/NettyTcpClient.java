package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-17 15:14
 **/

public class NettyTcpClient {
    private MyConnectManager manager = new MyConnectManager();

    public MyFuture<MessageResponse> send(Endpoint endpoint,MessageRequest request) throws Exception {
        Channel channel = manager.getChannel(endpoint);
        channel.writeAndFlush(request);
        MyFuture<MessageResponse> future = new MyFuture<>();
        Holder.putRequest(request.getMessageId(),future);
        return future;
    }

}
