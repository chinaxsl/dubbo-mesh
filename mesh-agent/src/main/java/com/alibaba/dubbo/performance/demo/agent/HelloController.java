package com.alibaba.dubbo.performance.demo.agent;

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

@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private RpcClient rpcClient = new RpcClient(registry);
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private AsyncHttpClient asyncHttpClient = Dsl.asyncHttpClient();
    private HashMap<Endpoint,String> urlMap = new HashMap<>();
    private Executor executor = Executors.newFixedThreadPool(256);
//    @RequestMapping(value = "")
//    public Object invoke(@RequestParam("interface") String interfaceName,
//                               @RequestParam("method") String method,
//                               @RequestParam("parameterTypesString") String parameterTypesString,
//                               @RequestParam("parameter") String parameter) throws Exception {
//        String type = System.getProperty("type");   // 获取type参数
//        if ("consumer".equals(type)) {
//            return consumer(interfaceName, method, parameterTypesString, parameter);
//        } else if ("provider".equals(type)) {
//            return provider(interfaceName, method, parameterTypesString, parameter);
//        } else {
//            return "Environment variable type is needed to set to provider or consumer.";
//        }
//    }
    @RequestMapping(value = "")
    public DeferredResult<Object> invoke(@RequestParam("interface") String interfaceName,
                                       @RequestParam("method") String method,
                                       @RequestParam("parameterTypesString") String parameterTypesString,
                                       @RequestParam("parameter") String parameter) throws Exception {
        return consumer(interfaceName, method, parameterTypesString, parameter);
    }

    @RequestMapping(value = "provider")
    public Object invoke1(@RequestParam("interface") String interfaceName,
                          @RequestParam("method") String method,
                          @RequestParam("parameterTypesString") String parameterTypesString,
                          @RequestParam("parameter") String parameter) throws Exception {
        return provider(interfaceName, method, parameterTypesString, parameter);
    }
    public byte[] provider(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {
//        DeferredResult<Object> result = new DeferredResult<>();
//        Runnable callable = () -> {
//            try {
//                Object value = rpcClient.invoke(interfaceName,method,parameterTypesString,parameter);
//                result.setResult(value);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        };
//        new Thread(callable).start();
        byte[] result = (byte[]) rpcClient.invoke(interfaceName, method, parameterTypesString, parameter);
        return result;
    }

    public DeferredResult<Object> consumer(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {
        if (null == endpoints){
            synchronized (lock){
                if (null == endpoints){
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
            for (Endpoint endpoint : endpoints) {
                urlMap.put(endpoint,"http://" + endpoint.getHost() + ":" + endpoint.getPort() + "/provider");
            }
        }

        DeferredResult<Object> result = new DeferredResult<>();
        org.asynchttpclient.Request request = Dsl.post(urlMap.get(LoadBalanceChoice.roundChoice(endpoints)))
                .addFormParam("interface",interfaceName)
                .addFormParam("method",method)
                .addFormParam("parameterTypesString",parameterTypesString)
                .addFormParam("parameter",parameter)
                .build();
        ListenableFuture<Response> responseListenableFuture = asyncHttpClient.executeRequest(request);
        Runnable callback = () -> {
            try {
                String responseBody = responseListenableFuture.get().getResponseBody();
                result.setResult(Integer.parseInt(responseBody));
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        responseListenableFuture.addListener(callback,executor);
        return result;
    }
}
