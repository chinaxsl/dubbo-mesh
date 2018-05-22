package com.alibaba.dubbo.performance.demo.agent.agent;/**
 * Created by msi- on 2018/5/18.
 */

import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageRequest;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MessageResponse;
import com.alibaba.dubbo.performance.demo.agent.agent.model.MyFuture;
import com.alibaba.dubbo.performance.demo.agent.agent.util.Common;
import com.alibaba.dubbo.performance.demo.agent.agent.util.IdGenerator;
import com.alibaba.dubbo.performance.demo.agent.registry.Endpoint;
import com.alibaba.dubbo.performance.demo.agent.registry.LoadBalanceChoice;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.springframework.http.HttpHeaders.CONTENT_LENGTH;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-18 20:33
 **/

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
//    private Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private FullHttpRequest request;
    private static NettyTcpClient tcpClient = new NettyTcpClient();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        request = fullHttpRequest;
        Map<String,String> paramMap = new HashMap<>();
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(fullHttpRequest);
        decoder.offer(fullHttpRequest);
        for (InterfaceHttpData data : decoder.getBodyHttpDatas()) {
            Attribute attribute = (Attribute) data;
            paramMap.put(attribute.getName(),attribute.getValue());
        }
        String messageId = IdGenerator.getIdByUUID();
        MessageRequest messageRequest = new MessageRequest(
                messageId,paramMap.get("interface"),paramMap.get("method"),paramMap.get("parameterTypesString"),paramMap.get("parameter")
                );
        Endpoint endpoint = LoadBalanceChoice.findRandom("com.alibaba.dubbo.performance.demo.provider.IHelloService");
        MyFuture<MessageResponse> future = tcpClient.send(endpoint,messageRequest);
        Runnable runnable = () -> {
            try {
                MessageResponse response = future.get();
                if (!writeResponse(fullHttpRequest,channelHandlerContext, (Integer) response.getResultDesc())) {
                    channelHandlerContext.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
                }
            } catch (Exception e) {
                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_GATEWAY
                );
                channelHandlerContext.writeAndFlush(response);
                e.printStackTrace();
            }
        };
        future.addListener(runnable,null);
    }

    private boolean writeResponse(HttpObject httpObject, ChannelHandlerContext ctx, int data) {
        boolean keepAlive = HttpUtil.isKeepAlive(request);
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                httpObject.decoderResult().isSuccess()? HttpResponseStatus.OK : HttpResponseStatus.BAD_REQUEST,
                Unpooled.copiedBuffer(String.valueOf(data).getBytes()));
        if (keepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        }
//        response.content().writeBytes(String.valueOf(data).getBytes());
        response.headers().add(CONTENT_TYPE,"application/json;charset=utf-8");
        response.headers().add(CONTENT_LENGTH,response.content().readableBytes());
        ctx.writeAndFlush(response);
        return keepAlive;
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
