package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

/**
 * @program: dubbo-mesh
 * @description: kryo消息编码器
 * @author: XSL
 * @create: 2018-05-18 16:25
 **/

public class KryoEncoder extends MessageEncoder {
    public KryoEncoder(KryoCodeUtil codeUtil) {
        super(codeUtil);
    }
}
