package com.alibaba.dubbo.performance.demo.agent.agent.model;/**
 * Created by msi- on 2018/5/16.
 */

import java.io.Serializable;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-16 21:27
 **/

public class MessageResponse implements Serializable {
    private String messageId;
    private Object resultDesc;

    public MessageResponse(String messageId, Object resultDesc) {
        this.messageId = messageId;
        this.resultDesc = resultDesc;
    }

    public String getMessageId() {
        return messageId;
    }

    public Object getResultDesc() {
        return resultDesc;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "messageId='" + messageId + '\'' +
                ", resultDesc=" + resultDesc +
                '}';
    }
}
