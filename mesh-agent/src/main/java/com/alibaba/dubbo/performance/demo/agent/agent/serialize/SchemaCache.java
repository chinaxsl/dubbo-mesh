package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/25.
 */

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @program: dubbo-mesh
 * @description: protostuff序列化shema
 * @author: XSL
 * @create: 2018-05-25 15:29
 **/

public class SchemaCache {
    private static class SchemaCacheHolder{
        private static SchemaCache cache = new SchemaCache();
    }

    public static SchemaCache getInstance() {
        return SchemaCacheHolder.cache;
    }

    private Cache<Class<?>,Schema<?>> cache = CacheBuilder.newBuilder()
            .maximumSize(1024).expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    private Schema<?> get(final Class<?> cls,Cache<Class<?>,Schema<?>> cache) {
        try {
            return cache.get(cls, () -> RuntimeSchema.createFrom(cls));
        } catch (Exception e) {
            return null;
        }
    }

    public Schema<?> get(final Class<?> cls) {
        return get(cls,cache);
    }
}
