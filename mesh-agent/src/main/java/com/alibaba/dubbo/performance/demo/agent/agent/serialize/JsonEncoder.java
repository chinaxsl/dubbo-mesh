package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

/**
 * @program: dubbo-mesh
 * @description: 使用fastJson实现编码
 * @author: XSL
 * @create: 2018-05-18 16:41
 **/

public class JsonEncoder extends MessageEncoder {
    public JsonEncoder(JsonCodeUtil codeUtil) {
        super(codeUtil);
    }
}
