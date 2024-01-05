package com.example.demo.redis.util;



import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * @author: zhouzhihu
 * @date: 2023/08/05 16:41
 */
public abstract class TenantCallable<T> implements Callable<T> {

    private CountDownLatch countDownLatch;

    protected abstract T execute() throws Exception;

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public T call() throws Exception {
        try {
            return execute();
        } catch (RuntimeException biz) {
            //todo 日志
            throw biz;
        } catch (Throwable t) {
            //todo 日志
            throw t;
        } finally {
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
        }
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
