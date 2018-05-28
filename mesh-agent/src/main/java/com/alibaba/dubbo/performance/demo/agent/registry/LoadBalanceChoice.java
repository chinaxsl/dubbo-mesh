package com.alibaba.dubbo.performance.demo.agent.registry;/**
 * Created by msi- on 2018/5/6.
 */


import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: dubbo-mesh
 * @description: 负载均衡实现
 * @author: XSL
 * @create: 2018-05-06 11:07
 **/

public class LoadBalanceChoice {
    private static final Random random = new Random();
    private static int pos = 0;
    private static EtcdRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private static List<Endpoint> endpoints;
    private static Object lock = new Object();
    private static Map<String,Integer>endsMap = new HashMap<>();
    static {
        endsMap.put("10.10.10.3",1);
        endsMap.put("10.10.10.4",4);
        endsMap.put("10.10.10.5",3);

    }
    private LoadBalanceChoice() {
    }

    public static Endpoint findRandom(String serviceName) throws Exception {
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = registry.find(serviceName);
                }
            }
        }
        return LoadBalanceChoice.randomChoice(endpoints);
    }

    public static Endpoint findRound(String serviceName) throws Exception {
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = registry.find(serviceName);
                }
            }
        }
        return LoadBalanceChoice.roundChoice(endpoints);
    }

    public static Endpoint findWeighted(String serviceName) throws Exception {
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = registry.find(serviceName);
                }
            }
        }
        List<Endpoint> endpointList = new ArrayList<>();
        for (Endpoint endpoint : endpoints) {
            for (int i = 0; i < endsMap.get(endpoint.getHost()); i++) {
                Endpoint endpoint1 = new Endpoint(endpoint.getHost(),endpoint.getPort());
                endpointList.add(endpoint1);
            }
        }
        return roundChoice(endpointList);
    }

    public static Endpoint weightedChoice(List<Endpoint> endpointList) {
        if (null == endpoints) {
            synchronized (lock) {
                if (null == endpoints) {
                    endpoints = new ArrayList<>();
                    for (Endpoint endpoint : endpointList) {
                        for (int i = 0; i < endsMap.get(endpoint.getHost()); i++) {
                            Endpoint endpoint1 = new Endpoint(endpoint.getHost(), endpoint.getPort());
                            endpoints.add(endpoint1);
                        }
                    }
                }
            }
        }
        return roundChoice(endpoints);
    }

    public static Endpoint randomChoice(List<Endpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    public static Endpoint roundChoice(List<Endpoint> endpoints) {
        return endpoints.get((pos++) % endpoints.size());
    }

}
