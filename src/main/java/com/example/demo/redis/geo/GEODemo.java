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
    //最后的<=1单位是km
//    select * from `crm_visit_record` where (
//            acos(
//            sin((30.288004*3.1415)/180) * sin((out_latitude*3.1415)/180) +
//    cos((30.288004*3.1415)/180) * cos((out_latitude*3.1415)/180) * cos((120.015488*3.1415)/180 - (out_longitude*3.1415)/180)
//            )*6370.996
//            )<=1;


    //最后20000是m 20km以内
//    SELECT
//    a.id,
//    a.region_id,
//    a.store_name,
//    a.store_code,
//    a.store_short_code,
//    a.store_tel,
//    a.is_business,
//    a.open_time,
//    a.is_support_delivery,
//    a.delivery_time,
//    a.delivery_range,
//    a.is_auto_pickup,
//    a.default_delivery_price,
//    a.free_delivery_price,
//    a.delivery_amount,
//    a.province,
//    a.city,
//    a.area,
//    a.address,
//    a.longitude,
//    a.latitude,
//    b.distance
//    FROM inm_store_store a
//    INNER JOIN (
//            SELECT id,
//            st_distance ( point ( longitude, latitude ),point (120.51, 30.12 ))* 111195 distance
//    FROM inm_store_store
//        ) b
//    ON a.id = b.id
//    WHERE a.is_delete = 0
//    AND a.is_business = 1
//    AND a.latitude IS NOT NULL
//    AND a.longitude IS NOT NULL
//    AND b.distance  <=  20000
//    ORDER BY b.distance
//    LIMIT 5

    public static void main(String[] args) {
        calDistance(120.1256,30.1251,120.1256,31.1251);
    }
}