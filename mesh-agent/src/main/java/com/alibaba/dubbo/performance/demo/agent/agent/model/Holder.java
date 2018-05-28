package com.alibaba.dubbo.performance.demo.agent.agent.model;/**
 * Created by msi- on 2018/5/13.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-13 20:46
 **/

public class Holder {
    private final static ConcurrentHashMap<String,MessageFuture<MessageResponse>> requestMap  = new ConcurrentHashMap<>();
    public static MessageFuture<MessageResponse> removeRequest(String key){
        return requestMap.remove(key);
    }
    public static void putRequest(String key, MessageFuture<MessageResponse> future) {
        requestMap.put(key,future);
    }


}
