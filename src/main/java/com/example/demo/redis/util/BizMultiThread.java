package com.example.demo.redis.util;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多线程处理模板
 * case:
 * 原查询函数
 * xxxxClient.queryByCodes(codes)
 * 多线程处理 BizMultiThread.start.list(codes,30).execute(e->
 * xxxxClient.queryByCodes(e)).getList();
 *
 * @author: zhouzhihu
 * @date: 2023/08/05 16:29
 */
public class BizMultiThread {

    /**
     * slf4j
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(BizMultiThread.class);

    /**
     * 现场名称前缀
     */
    private static final String THREAD_PREFIX = "inm-renata-multi-thread-";

    /**
     * 初始化线程池大小
     */
    private static final int CORE_POOL_SIZE = 40;

    /**
     * 最大线程池大小
     */
    private static final int MAX_POOL_SIZE = 40;

    /**
     * 线程空闲时长(分钟)
     */
    private static final long KEEP_ALIVE_TIME = 5;

    /**
     * 默认任务分区大小
     */
    private static final int DEFAULT_PARTITION_SIZE = 200;

    /**
     * 线程池
     */
    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE,
            KEEP_ALIVE_TIME,
            TimeUnit.MINUTES, new LinkedBlockingQueue<>(4096),
            new ThreadFactoryBuilder().setNameFormat(THREAD_PREFIX + "%d").build());

    public static Task start() {
        return new Task();
    }

    public static Task quickStart(List l) {
        return start().list(l);
    }

    public static Task quickStart(List l, int size) {
        return start().list(l).size(size);
    }

    /**
     * 任务执行器
     */
    public static class Task {
        List splitParams = new ArrayList();
        int maxPartitionSize = DEFAULT_PARTITION_SIZE;

        SupplierHandler handler;
        List<Future> futures = new ArrayList<>();

        private Task() {
        }

        /**
         * 需要分割的list
         *
         * @param l
         * @return
         */
        public Task list(List l) {
            if (l != null) {
                splitParams = l;
            }
            return this;
        }

        /**
         * 可选的分割后的子list的最大size 默认200
         *
         * @param maxPartitionSize
         * @return
         */
        public Task size(int maxPartitionSize) {
            if (maxPartitionSize > 0) {
                this.maxPartitionSize = maxPartitionSize;
            }
            return this;
        }

        /**
         * 可选的异常处理 默认 throw BizException
         *
         * @param supplier
         * @return
         */
        public Task handler(SupplierHandler supplier) {
            if (supplier != null) {
                this.handler = supplier;
            }
            return this;
        }

        public Task execute(SupplierExecute supplier) {
            // eagle eye 上下文
            if (StringUtils.startsWith(Thread.currentThread().getName(), THREAD_PREFIX)) {
//                InmLogger.build()
//                        .build("currentName", Thread.currentThread().getName())
//                        .error("INM_MULTI_THREAD_NEST_CALL, 线程池不允许嵌套调用");
                throw new RuntimeException(
                        "线程池不允许嵌套调用, 当前线程：" + Thread.currentThread().getName());
            }

            List<List> l = Lists.partition(splitParams, maxPartitionSize);
            for (List param : l) {
                futures.add(BizMultiThread.THREAD_POOL.submit(new TenantCallable<Object>() {
                    @Override
                    protected Object execute() {
                        return supplier.execute(param);
                    }
                }));
            }
            return this;
        }

        public <T> List<T> getList() {
            List<T> result = new ArrayList<>();
            for (Future f : futures) {
                try {
                    Object o = f.get();
                    if (o instanceof List) {
                        result.addAll((Collection<? extends T>) o);
                    }
                } catch (Throwable e) {
                    doHandle(e);
                }
            }
            return result;
        }

        public <T> List<T> getList(int size) {
            List<T> result = new ArrayList<>(size);
            for (Future f : futures) {
                try {
                    Object o = f.get();
                    if (o instanceof List) {
                        result.addAll((Collection<? extends T>) o);
                    }
                } catch (Throwable e) {
                    doHandle(e);
                }
            }
            return result;
        }

        public <K, V> Map<K, V> getMap() {
            Map<K, V> result = new HashMap<>();
            for (Future f : futures) {
                try {
                    Object o = f.get();
                    if (o instanceof Map) {
                        result.putAll((Map<? extends K, ? extends V>) o);
                    }
                } catch (Throwable e) {
                    doHandle(e);
                }
            }
            return result;
        }

        public <K, V> Map<K, V> getFlatMap() {
            Map<K, V> result = new HashMap<>();
            for (Future f : futures) {
                try {
                    Object o = f.get();
                    if (o instanceof Map) {

                        Map<? extends K, ? extends V> content = (Map<? extends K, ? extends V>) o;

                        for (Map.Entry<? extends K, ? extends V> entry : content.entrySet()) {

                            if (result.containsKey(entry.getKey())) {

                                V value = result.get(entry.getKey());

                                if ((value instanceof List) && (entry.getValue() instanceof List)) {
                                    ((List) value).addAll((List) entry.getValue());
                                } else {
                                    throw new RuntimeException(
                                            "flat map仅支持list类型的value" + Thread.currentThread().getName());
                                }

                                result.put(entry.getKey(), value);
                            } else {
                                result.put(entry.getKey(), entry.getValue());
                            }
                        }
                    }
                } catch (Throwable e) {
                    doHandle(e);
                }
            }
            return result;
        }

        private void doHandle(Throwable e) {
            if (handler == null) {
               // InmLogger.build().error(LOGGER, e.getMessage(), e);
                throw new RuntimeException("并行查询失败" + getClass().getName() + ":" + e.getMessage());
            } else {
                handler.execute(e, LOGGER);
            }
        }

        @FunctionalInterface
        public interface SupplierExecute {
            Object execute(List param);
        }

        @FunctionalInterface
        public interface SupplierHandler {
            void execute(Throwable e, Logger logger);
        }
    }
}
