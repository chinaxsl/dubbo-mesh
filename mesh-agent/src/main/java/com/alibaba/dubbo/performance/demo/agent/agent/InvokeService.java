package com.alibaba.dubbo.performance.demo.agent.agent;
/**
 * Created by msi- on 2018/5/17.
 */

import com.alibaba.dubbo.performance.demo.agent.registry.EtcdRegistry;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-17 15:59
 **/

public class InvokeService {
    private static EtcdRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
    private InvokeService() {
    }
    public static void init() {}
//    public static void execute(Runnable callable) {
//        executor.execute(callable);
//    }

}
