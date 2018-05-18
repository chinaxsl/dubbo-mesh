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
    private LoadBalanceChoice() {
    }
    public static Endpoint randomChoice(List<Endpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    public static Endpoint roundChoice(List<Endpoint> endpoints) {
        return endpoints.get((pos++) % endpoints.size());
    }
}
