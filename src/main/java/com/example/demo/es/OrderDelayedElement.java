package com.example.demo.es;

import lombok.Data;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName: parking_road
 * @Package: com.vinsuan.parking.platform.service.delay
 * @ClassName: OrderDelayedElement
 * @Author: RU
 * @Description: 支付订单延迟属性类
 * @Date: 2020/3/12 16:14
 * @Version: 1.0
 */
@Data
public class OrderDelayedElement implements Delayed {

    /**
     * 延迟时间
     */
    private  Long expire;
    /**
     * 支付订单id
     */
    private  Integer id;

    public OrderDelayedElement(){}
    public OrderDelayedElement(Long expire, Integer id) {
        this.expire = System.currentTimeMillis() + expire;
        this.id = id;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed o) {
        return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    }
}
