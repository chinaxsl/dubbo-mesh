package com.alibaba.dubbo.performance.demo.agent.registry;/**
 * Created by msi- on 2018/5/6.
 */


import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    private static ConcurrentLinkedQueue<Endpoint> chooseQueue = new ConcurrentLinkedQueue<>();
    private static List<Endpoint> newEndpoints;
    static {
        endsMap.put("10.10.10.3",2);
        endsMap.put("10.10.10.4",4);
        endsMap.put("10.10.10.5",6);
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
        if (newEndpoints == null) {
            synchronized (lock) {
                if (newEndpoints == null ) {
                    newEndpoints = new ArrayList<>();
                    for (Endpoint endpoint1 : endpoints) {
                        for (int i = 0; i < endsMap.get(endpoint1.getHost()); i++) {
                            Endpoint endpoint2 = new Endpoint(endpoint1.getHost(), endpoint1.getPort());
                            newEndpoints.add(endpoint2);
                        }
                    }
                }
            }
        }
        Endpoint endpoint = chooseQueue.poll();
        if (endpoint == null) {
            List<Endpoint> endpointList = new ArrayList<>(newEndpoints);
            Collections.shuffle(endpointList);
            if (chooseQueue.size() == 0) {
                chooseQueue = new ConcurrentLinkedQueue<>(endpointList);
            }
            endpoint = chooseQueue.poll();
        }
        return endpoint;
    }

//    public static Endpoint weightedChoice(List<Endpoint> endpointList) {
//        if (null == endpoints) {
//            synchronized (lock) {
//                if (null == endpoints) {
//                    endpoints = new ArrayList<>();
//                    for (Endpoint endpoint : endpointList) {
//                        for (int i = 0; i < endsMap.get(endpoint.getHost()); i++) {
//                            Endpoint endpoint1 = new Endpoint(endpoint.getHost(), endpoint.getPort());
//                            endpoints.add(endpoint1);
//                        }
//                    }
//                }
//            }
//        }
//        return roundChoice(endpoints);
//    }

    public static Endpoint randomChoice(List<Endpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    public static Endpoint roundChoice(List<Endpoint> endpoints) {
        return endpoints.get((pos++) % endpoints.size());
    }

}
