package com.alibaba.dubbo.performance.demo.agent;

import com.alibaba.dubbo.performance.demo.agent.dubbo.RpcClient;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.IRegistry;
import com.alibaba.dubbo.performance.demo.agent.registry.LoadBalanceChoice;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

@RestController
public class HelloController {

    private Logger logger = LoggerFactory.getLogger(HelloController.class);
    
    private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private RpcClient rpcClient = new RpcClient(registry);
    private List<Endpoint> endpoints = null;
    private Object lock = new Object();
    private OkHttpClient httpClient = new OkHttpClient();
    private HashMap<Endpoint,String> urlMap = new HashMap<>();

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
    public WebAsyncTask<Object> invoke(@RequestParam("interface") String interfaceName,
                                       @RequestParam("method") String method,
                                       @RequestParam("parameterTypesString") String parameterTypesString,
                                       @RequestParam("parameter") String parameter) {
        Callable<Object> callable = ()-> {
            String type = System.getProperty("type");
            if ("consumer".equals(type)) {
                consumer(interfaceName, method, parameterTypesString, parameter);
            } else if ("provider".equals(type)) {
                provider(interfaceName, method, parameterTypesString, parameter);
            }
            return "error";
        };
        return new WebAsyncTask<Object>(callable);
    }
    public byte[] provider(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {

        Object result = rpcClient.invoke(interfaceName,method,parameterTypesString,parameter);
        return (byte[]) result;
    }

    public Integer consumer(String interfaceName,String method,String parameterTypesString,String parameter) throws Exception {

        if (null == endpoints){
            synchronized (lock){
                if (null == endpoints){
                    endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
                }
            }
            for (Endpoint endpoint : endpoints) {
                urlMap.put(endpoint,"http://" + endpoint.getHost() + ":" + endpoint.getPort());
            }
        }

        // 简单的负载均衡，随机取一个
        Endpoint endpoint = LoadBalanceChoice.roundChoice(endpoints);

//        String url =  "http://" + endpoint.getHost() + ":" + endpoint.getPort();

        RequestBody requestBody = new FormBody.Builder()
                .add("interface",interfaceName)
                .add("method",method)
                .add("parameterTypesString",parameterTypesString)
                .add("parameter",parameter)
                .build();

        Request request = new Request.Builder()
                .url(urlMap.get(endpoint))
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            byte[] bytes = response.body().bytes();
            String s = new String(bytes);
            return Integer.valueOf(s);
        }
    }
}
