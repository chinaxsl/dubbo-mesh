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
    private static HashMap<Integer, LinkedList<Long>> timeMap = new HashMap<Integer, LinkedList<Long>>();
    private static final int MAX_AVAGE_TIMES = 5;
    private static final Random random = new Random();
    private static int pos = 0;
    private final static Object[] locks = new Object[MAX_AVAGE_TIMES];
    private static final List<Endpoint> pickList = new ArrayList<>();
    private static final List<Endpoint> endList = new ArrayList<>();

    //    static {
//        for (int i = 0; i < locks.length; i++) {
//            locks[i] = new Object();
//        }
//    }
    private LoadBalanceChoice() {
    }
    public static Endpoint randomChoice(List<Endpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    public static Endpoint roundChoice(List<Endpoint> endpoints) {
        int x = pos % endpoints.size();
        pos ++;
        return endpoints.get(x);
    }
    /**
    * @Description: 加权随机法实现负载均衡
    * @Param: [endpoints]
    * @return: com.alibaba.dubbo.performance.demo.agent.registry.Endpoint
    */
//    public static Endpoint weightRandomChoice(List<Endpoint> endpoints) {
//        if (!endpoints.equals(endList)) {
//            pickList.clear();
//            endList.clear();
//            for (Endpoint endpoint : endpoints) {
//                int w = endpoint.getWeight();
//                for (int i = 0; i < w; i++) {
//                    pickList.add(endpoint);
//                }
//            }
//            endList.addAll(endpoints);
//        }
//        return pickList.get(random.nextInt(pickList.size()));
//    }

    /**
    * @Description: 最短时间负载均衡策略
    * @Param: [endpoints]
    * @return: com.alibaba.dubbo.performance.demo.agent.registry.Endpoint
    */
//    public static Endpoint minTimeChoice(List<Endpoint> endpoints){
//        if (pos.get() < 50) {
//            return roundChoice(endpoints);
//        }
//        List<Long> timeList;
//        double minTime = Double.MAX_VALUE;
//        Endpoint minTimeEndopint = null;
//        for (Endpoint endpoint : endpoints) {
//            int port = endpoint.getPort();
//            if (!timeMap.containsKey(port)) {
//                timeMap.put(port,new LinkedList<Long>());
//            }
//            timeList = timeMap.get(port);
////            List<Long> workList = timeList.subList(0,timeList.size());
//            long totalTime = 0l;
//            if (timeList.isEmpty()) {
//                continue;
//            }
//            for (Long aLong : timeList) {
//                totalTime += aLong;
//            }
//            totalTime = totalTime / timeList.size();
//            if (totalTime < minTime) {
//                minTime = totalTime;
//                minTimeEndopint = endpoint;
//            }
//        }
//        if (minTimeEndopint == null) {
//            return randomChoice(endpoints);
//        }
//        return minTimeEndopint;
//    }

    public static void addTime(int port ,long time) {
        if (!timeMap.containsKey(port)) {
            timeMap.put(port,new LinkedList<>());
        }
        LinkedList<Long> longs = timeMap.get(port);
//        synchronized (locks[port % MAX_AVAGE_TIMES]) {
            if (longs.size() >= MAX_AVAGE_TIMES) {
//                synchronized (locks[port % MAX_AVAGE_TIMES]) {
                    longs.add(time);
                    longs.poll();
//                }
            }
            else {
//                synchronized (locks[port % MAX_AVAGE_TIMES]) {
                    longs.add(time);
//                }
//            }
        }
    }
}
