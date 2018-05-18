package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @program: dubbo-mesh
 * @description: 使用fastJson序列化/反序列化消息
 *               反序列化时要特别注意！反序列化根据type的类型做反序列化
 * @author: XSL
 * @create: 2018-05-18 16:30
 **/

public class JsonCodeUtil implements MessageCodeUtil {
    private Logger logger = LoggerFactory.getLogger(JsonCodeUtil.class);
    private Type type;
    public JsonCodeUtil(Type type) {
        this.type = type;
    }
    private final static JsonCodeUtil requestCodeUtil = new JsonCodeUtil(MessageRequest.class);
    private final static JsonCodeUtil responseCodeUtil = new JsonCodeUtil(MessageResponse.class);

    public static JsonCodeUtil getRequestCodeUtil() {
        return requestCodeUtil;
    }

    public static JsonCodeUtil getResponseCodeUtil() {
        return responseCodeUtil;
    }

    @Override
    public void encode(ByteBuf out, Object message) throws IOException {
        byte[] body = JSON.toJSONBytes(message);
        out.writeInt(body.length);
        out.writeBytes(body);
//        logger.info("" + body.length) ;
    }

    @Override
    public Object decode(byte[] body) throws IOException {
        return JSON.parseObject(body,type);
    }
}
