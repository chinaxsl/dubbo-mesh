package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/25.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @program: dubbo-mesh
 * @description:使用protostuff进行序列化和反序列化
 * @author: XSL
 * @create: 2018-05-25 15:42
 **/

public class ProtostuffSerialize implements MessageSerialize {
    private static SchemaCache cachedSchema = SchemaCache.getInstance();
    private static Objenesis objenesis = new ObjenesisStd(true);
    private boolean rpcDirect = false;

    public boolean isRpcDirect() {
        return rpcDirect;
    }

    public void setRpcDirect(boolean rpcDirect) {
        this.rpcDirect = rpcDirect;
    }

    public static <T> Schema<T> getCachedSchema(Class<T> tClass) {
        return (Schema<T>)cachedSchema.get(tClass);
    }

    @Override
    public void serialize(OutputStream outputStream, Object object) throws IOException {
        Class cls = object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema schema = getCachedSchema(cls);
            ProtostuffIOUtil.writeTo(outputStream,object,schema,buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public Object deserialize(InputStream inputStream) throws IOException {
        try {
            Class cls = isRpcDirect() ? MessageRequest.class: MessageResponse.class;
            Object mesage = objenesis.newInstance(cls);
            Schema<Object> schema = getCachedSchema(cls);
            ProtostuffIOUtil.mergeFrom(inputStream,mesage,schema);
            return mesage;
        } catch (Exception e){
            throw new IllegalStateException(e.getMessage(),e);
        }
    }
}
