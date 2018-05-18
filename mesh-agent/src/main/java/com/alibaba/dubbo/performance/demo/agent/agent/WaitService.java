package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.Holder;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.dubbo.model.RpcFuture;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-17 15:59
 **/

public class WaitService {
    private static Executor executor = Executors.newFixedThreadPool(256,Executors.defaultThreadFactory());
    private static EtcdRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private static RpcClient rpcClient = new RpcClient(registry);


    public static void init() {

    }
    public static void execute(Runnable callable) {
        executor.execute(callable);
    }
    public static RpcFuture executeInvoke(MessageRequest messageRequest) throws Exception {
        RpcFuture future = (RpcFuture) rpcClient.invoke(messageRequest.getInterfaceName(),messageRequest.getMethod(),messageRequest.getParameterTypesString(),messageRequest.getParameter());
//        Holder.putResult(messageRequest.getMessageId(),RpcFuture);
        return future;
    }
}