package com.example.demo.redis.geo;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 根据2点经纬度算距离
 */
public class GEODemo {

    private static final BigDecimal EARTH_RADIUS = new BigDecimal("6370.996");
    private static double rad(double d){
        return d * Math.PI / 180.0;
    }

    public static void calDistance(double lng1, double lat1, double lng2, double lat2){
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +
                Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));

        BigDecimal tempDecimal = new BigDecimal(s).multiply(EARTH_RADIUS);

        BigDecimal result = new BigDecimal(Math.round(tempDecimal.multiply(new BigDecimal("10000")).doubleValue()))
                .divide(new BigDecimal("10000"), 2, RoundingMode.HALF_UP);
        System.out.println(result);

    }

    public static void main(String[] args) {
        calDistance(120.1256,30.1251,120.1256,31.1251);
    }
}
