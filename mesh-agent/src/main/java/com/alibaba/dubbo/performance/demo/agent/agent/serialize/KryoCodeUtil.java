package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

import com.esotericsoftware.kryo.pool.KryoPool;
import com.google.common.io.Closer;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-18 15:29
 **/

public class KryoCodeUtil implements MessageCodeUtil {
//    private Logger logger = LoggerFactory.getLogger(KryoCodeUtil.class);
    private KryoPool pool;
    private KryoSerialize kryoSerialize;
    public KryoCodeUtil(KryoPool pool) {
        this.pool = pool;
        this.kryoSerialize = new KryoSerialize(pool);
    }

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            kryoSerialize.serialize(byteArrayOutputStream,message);
            byte[] body = byteArrayOutputStream.toByteArray();
            out.writeInt(body.length);
            out.writeBytes(body);
        } finally {
            if (byteArrayOutputStream!=null)
                byteArrayOutputStream.close();
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        ByteArrayInputStream byteArrayInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(body);
            return kryoSerialize.deserialize(byteArrayInputStream);
        } finally {
            if (byteArrayInputStream!=null)
                byteArrayInputStream.close();
        }
    }
}
