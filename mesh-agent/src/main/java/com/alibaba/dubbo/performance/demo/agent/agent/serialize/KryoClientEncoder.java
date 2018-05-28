package com.alibaba.dubbo.performance.demo.agent.agent.serialize;/**
 * Created by msi- on 2018/5/27.
 */

import io.netty.channel.ChannelHandler;

/**
 * @program: dubbo-mesh
 * @description:
 * @author: XSL
 * @create: 2018-05-27 13:08
 **/
public class KryoClientEncoder extends MessageClientEncoder {
    public KryoClientEncoder(MessageCodeUtil codeUtil) {
        super(codeUtil);
    }
}
