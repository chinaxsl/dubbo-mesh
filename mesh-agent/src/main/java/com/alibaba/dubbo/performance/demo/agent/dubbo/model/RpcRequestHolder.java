package com.alibaba.dubbo.performance.demo.agent.dubbo.model;

import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;

import java.util.concurrent.ConcurrentHashMap;

public class RpcRequestHolder {

    // key: requestId     value: RpcFuture
    private static ConcurrentHashMap<String,MyFuture> processingRpc = new ConcurrentHashMap<>();

    public static void put(String requestId,MyFuture rpcFuture){
        processingRpc.put(requestId,rpcFuture);
    }

    public static MyFuture get(String requestId){
        return processingRpc.get(requestId);
    }

    public static MyFuture remove(String requestId){
        return processingRpc.remove(requestId);
    }
}
