package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/18.
 */

/**
 * @program: dubbo-mesh
 * @description: kryo消息解码器
 * @author: XSL
 * @create: 2018-05-18 16:27
 **/

public class KryoDecoder extends MessageDecoder {
    public KryoDecoder(KryoCodeUtil codeUtil) {
        super(codeUtil);
    }
}
