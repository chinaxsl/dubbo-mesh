package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

/**
 * @program: dubbo-mesh
 * @description: 使用fastjson实现解码器
 * @author: XSL
 * @create: 2018-05-18 16:43
 **/

public class JsonDecoder extends MessageDecoder {
    public JsonDecoder(JsonCodeUtil codeUtil) {
        super(codeUtil);
    }
}
