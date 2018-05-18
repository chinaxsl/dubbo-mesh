package com.alibaba.dubbo.performance.demo.agent.agent.model;/**
 * Created by msi- on 2018/5/13.
 */

import java.util.concurrent.*;

/**
 * @program: TcpProject
 * @description:
 * @author: XSL
 * @create: 2018-05-13 16:56
 **/

public class MyFuture<T> implements Future<T> {
    private CountDownLatch latch = new CountDownLatch(1);
    private T result;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        latch.await();
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await();
        return result;
    }

    public void done(T result){
        this.result = result;
        latch.countDown();
    }
}
