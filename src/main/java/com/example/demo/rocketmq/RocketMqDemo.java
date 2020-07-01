package com.example.demo.rocketmq;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: RU
 * @Date: 2020/4/29 14:59
 */
public class RocketMqDemo {

    private static final int size = 1000000;// 100万
    private static BloomFilter<Integer> bloomFilter =BloomFilter.create(Funnels.integerFunnel(), size,0.03);

    public static void main(String[] args) throws Exception{
//        //Instantiate with a producer group name.
//        DefaultMQProducer producer = new
//                DefaultMQProducer("please_rename_unique_group_name");
//        // Specify name server addresses.
//        producer.setNamesrvAddr("120.26.81.182:9876");
//        //Launch the instance.
//        producer.start();
//        Message msg = new Message("TopicTest" /* Topic */,
//                "TagA" /* Tag */,
//                ("Hello RocketMQ ").getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
//        );
//        //Call send message to deliver message to one of brokers.
//        SendResult sendResult = producer.send(msg);
//        System.out.printf("%s%n", sendResult);
//        //Shut down once the producer instance is not longer in use.
//        producer.shutdown();
        System.out.println();
        for (int i = 0; i < size; i++) {
            bloomFilter.put(i);
        }
        List<Integer> list = new ArrayList<>(1000);
        long currentTimeMillis = System.currentTimeMillis();
        //故意取10000个不在过滤器里的值，看看有多少个会被认为在过滤器里
        for (int i = size + 20000; i < size + 40000; i++) {
            if (bloomFilter.mightContain(i)) {
                list.add(i);
            }
        }
        long a = System.currentTimeMillis();
        System.out.println("消耗时间:"+(a-currentTimeMillis));
        System.out.println("误判的数量：" + list.size()); //在300左右
    }
}
