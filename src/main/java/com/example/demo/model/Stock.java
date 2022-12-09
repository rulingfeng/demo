package com.example.demo.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

public class Stock {
    private Integer id;

    private String name;

    private Integer count;

    private Integer sale;

    private Integer version;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", count=").append(count);
        sb.append(", sale=").append(sale);
        sb.append(", version=").append(version);
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        Integer pri = 125;
        BigDecimal bigDecimal = new BigDecimal("0.01");
        BigDecimal multiply = bigDecimal.multiply(new BigDecimal(pri));
        System.out.println(multiply);
    }
}