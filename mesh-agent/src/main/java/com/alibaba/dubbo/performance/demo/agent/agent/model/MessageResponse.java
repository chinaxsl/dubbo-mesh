package com.alibaba.dubbo.performance.demo.agent.agent.model;/**
 * Created by msi- on 2018/5/16.
 */

import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;

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
    private Endpoint endpoint;
    private int executingTask;
    private long sendTime;
    private boolean isSuccess = true;

    public MessageResponse(String messageId, Object resultDesc, Endpoint endpoint, int executingTask) {
        this.messageId = messageId;
        this.resultDesc = resultDesc;
        this.endpoint = endpoint;
        this.executingTask = executingTask;
        this.isSuccess = true;
    }

    public MessageResponse(String messageId, Object resultDesc, Endpoint endpoint, int executingTask, boolean isSuccess) {
        this.messageId = messageId;
        this.resultDesc = resultDesc;
        this.endpoint = endpoint;
        this.executingTask = executingTask;
        this.isSuccess = isSuccess;
    }

    public MessageResponse() {
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public int getExecutingTask() {
        return executingTask;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setResultDesc(Object resultDesc) {
        this.resultDesc = resultDesc;
    }

    public String getMessageId() {
        return messageId;
    }

    public Object getResultDesc() {
        return resultDesc;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "messageId='" + messageId + '\'' +
                ", resultDesc=" + resultDesc +
                '}';
    }
}
