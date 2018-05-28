package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/25.
 */

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-25 15:51
 **/

public class ProtostuffSerializeFactory extends BasePooledObjectFactory<ProtostuffSerialize> {
    @Override
    public ProtostuffSerialize create() throws Exception {
        return createProtostuff();
    }

    @Override
    public PooledObject<ProtostuffSerialize> wrap(ProtostuffSerialize protostuffSerialize) {
        return new DefaultPooledObject<ProtostuffSerialize>(protostuffSerialize);
    }

    private ProtostuffSerialize createProtostuff() {
        return new ProtostuffSerialize();
    }
}
