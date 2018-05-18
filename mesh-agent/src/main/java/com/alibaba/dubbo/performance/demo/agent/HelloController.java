package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.agent.NettyTcpClient;
import com.alibaba.dubbo.performance.demo.agent.agent.WaitService;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;
import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.LoadBalanceChoice;
import okhttp3.*;
import okhttp3.Request;
import org.asynchttpclient.*;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class HelloController {

//    private Logger logger = LoggerFactory.getLogger(HelloController.class);
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private RpcClient rpcClient = new RpcClient(registry);
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private AtomicInteger requestCount = new AtomicInteger(0);
    private NettyTcpClient nettyTcpClient = new NettyTcpClient();
//    private AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();
    private HashMap<Endpoint,String> urlMap = new HashMap<>();
//    private Executor executor = Executors.newFixedThreadPool(256);




    @RequestMapping(value = "")
    public DeferredResult<Object> invoke(@RequestParam("interface") String interfaceName,
                                       @RequestParam("method") String method,
                                       @RequestParam("parameterTypesString") String parameterTypesString,
                                       @RequestParam("parameter") String parameter) throws Exception {
        return consumer(interfaceName, method, parameterTypesString, parameter);
    }
    public DeferredResult<Object> consumer(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {
        if (null == endpoints){
            synchronized (lock){
                if (null == endpoints){
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
        }
        DeferredResult<Object> result = new DeferredResult<>();
        Endpoint endpoint = LoadBalanceChoice.randomChoice(endpoints);
        if (endpoint == null) {
            throw new RuntimeException("找不到该服务");
        }
//        logger.info("requestCount = " + requestCount.get() );
        MessageRequest request = new MessageRequest(String.valueOf(requestCount.getAndIncrement()),interfaceName,method,parameterTypesString,parameter);
        MyFuture<MessageResponse> future = nettyTcpClient.send(endpoint,request);
        Runnable callback = () -> {
            try {
                MessageResponse response = future.get();
                result.setResult(response.getResultDesc());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        WaitService.execute(callback);
        return result;
    }
}
