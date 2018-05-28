package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/25.
 */

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-25 16:07
 **/

public class ProtostuffCodeUtil implements MessageCodeUtil {
    private ProtostuffSerializePool pool = ProtostuffSerializePool.getProtostuffPoolInstance();
    private boolean rpcDirect = false;
    private static ProtostuffCodeUtil clientCodeUtil = new ProtostuffCodeUtil(false);
    private static ProtostuffCodeUtil serverCodeUtil = new ProtostuffCodeUtil(true);

    public static ProtostuffCodeUtil getClientCodeUtil() {
        return clientCodeUtil;
    }

    public static ProtostuffCodeUtil getServerCodeUtil() {
        return serverCodeUtil;
    }

    public ProtostuffCodeUtil(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }

    public ProtostuffCodeUtil() {
    }

    public boolean isRpcDirect() {
        return rpcDirect;
    }

    public void setRpcDirect(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            ProtostuffSerialize protostuffSerialize = pool.borrow();
            protostuffSerialize.serialize(byteArrayOutputStream,message);
            byte[] body = byteArrayOutputStream.toByteArray();
            int dataLength = body.length;
            out.writeInt(dataLength);
            out.writeBytes(body);
            pool.restore(protostuffSerialize);
        } finally {
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
        }
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        ByteArrayInputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayInputStream(body);
            ProtostuffSerialize serialize = pool.borrow();
            serialize.setRpcDirect(rpcDirect);
            Object object = serialize.deserialize(byteArrayOutputStream);
            pool.restore(serialize);
            return object;
        } finally {
            if (byteArrayOutputStream!=null) {
                byteArrayOutputStream.close();
            }
        }
    }
}
