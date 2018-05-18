package com.alibaba.dubbo.performance.demo.agent.agent.model;/**
 * Created by msi- on 2018/5/13.
 */

import java.io.Serializable;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-13 15:47
 **/

public class MessageRequest implements Serializable{
    private String messageId;
    private String interfaceName;
    private String method;
    private String parameterTypesString;
    private String parameter;

    public MessageRequest(String messageId, String interfaceName, String method, String parameterTypesString, String parameter) {
        this.messageId = messageId;
        this.interfaceName = interfaceName;
        this.method = method;
        this.parameterTypesString = parameterTypesString;
        this.parameter = parameter;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public String getMethod() {
        return method;
    }

    public String getParameterTypesString() {
        return parameterTypesString;
    }

    public String getParameter() {
        return parameter;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "messageId='" + messageId + '\'' +
                ", interfaceName='" + interfaceName + '\'' +
                ", method='" + method + '\'' +
                ", parameterTypesString='" + parameterTypesString + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
