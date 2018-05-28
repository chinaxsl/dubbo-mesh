package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/25.
 */

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-25 15:55
 **/

public class ProtostuffSerializePool {
    private GenericObjectPool<ProtostuffSerialize> protostuffPool;
    volatile private static ProtostuffSerializePool poolFactory = null;

    private ProtostuffSerializePool() {
        protostuffPool = new GenericObjectPool<ProtostuffSerialize>(new ProtostuffSerializeFactory());
    }

    public static ProtostuffSerializePool getProtostuffPoolInstance() {
        if (poolFactory == null) {
            synchronized (ProtostuffSerializePool.class) {
                if (poolFactory == null) {
                    poolFactory = new ProtostuffSerializePool();
                }
            }
        }
        return poolFactory;
    }

    public ProtostuffSerializePool(final int maxTotal,final int minIdle,final long maxWaitMillis,final long minEvictableIdleTimeMills) {
        protostuffPool = new GenericObjectPool<ProtostuffSerialize>(new ProtostuffSerializeFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMinIdle(minIdle);
        config.setMaxWaitMillis(maxWaitMillis);
        config.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMills);

        protostuffPool.setConfig(config);
    }

    public ProtostuffSerialize borrow() {
        try {
            return getProtostuffPool().borrowObject();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private GenericObjectPool<ProtostuffSerialize> getProtostuffPool() {
        return protostuffPool;
    }

    public void restore(final ProtostuffSerialize object) {
        getProtostuffPool().returnObject(object);
    }
}
