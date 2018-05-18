package com.alibaba.dubbo.performance.demo.agent.agent.model;/**
 * Created by msi- on 2018/5/13.
 */

import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-13 20:46
 **/

public class Holder {
    private final static ConcurrentHashMap<String,MyFuture<MessageResponse>> requestMap  = new ConcurrentHashMap<>();
//    private final static ConcurrentHashMap<String,MyFuture<Object>> clientResultMap = new ConcurrentHashMap<>();
    public static MyFuture<MessageResponse> removeRequest(String key){
        return requestMap.remove(key);
    }

    public static void putRequest(String key, MyFuture<MessageResponse> future) {
        requestMap.put(key,future);
    }

//    public static void putResult(String key,MyFuture<Object> future) {
//        clientResultMap.put(key,future);
//    }
}
