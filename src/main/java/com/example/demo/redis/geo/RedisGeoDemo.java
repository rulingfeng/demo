package com.example.demo.redis.geo;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Component
public class RedisGeoDemo {

    @Autowired
    private RedisTemplate redisTemplate;

    public void geoTest(){
        RedisGeoCommands.GeoRadiusCommandArgs arg = RedisGeoCommands.GeoRadiusCommandArgs
                .newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending();

        //需要查询的经纬度
        double searchLon = 120.417;
        //需要查询的纬度
        double searchLat = 123.666;
        //存的所有经纬度的key
        String key = "key";
        //查询的范围
        double range = 10.0;
        GeoResults cityGEO = redisTemplate.opsForGeo().radius(key
                , new Circle(new Point(searchLon, searchLat), new Distance(range, Metrics.KILOMETERS))
                , arg);

        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> content = cityGEO.getContent();
        if(CollectionUtil.isNotEmpty(content)){
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> list : content){
                //一般是存放车场的id，可以直接去数据库主键查询
                String id = list.getContent().getName();
                //redis里面的经度
                double lon = list.getContent().getPoint().getX();
                //redis里面的维度
                double lat = list.getContent().getPoint().getY();
                //搜索点到这个地点的距离
                BigDecimal searchDistance = new BigDecimal(list.getDistance().getValue() + "");
                //距离单位 km mi
                Metrics metric = (Metrics)list.getDistance().getMetric();


                //指定一个点，获取这个点和这个redis这个点的距离
                double userLon = 120.52;
                double userLat = 121.52;
                String userCoordinatesName = UUID.randomUUID().toString().replace("-", "").toUpperCase();
                //存入经纬度
                redisTemplate.opsForGeo().add(key,new Point(userLon,userLat),userCoordinatesName);
                Distance userDistance = redisTemplate.opsForGeo().distance(key, userCoordinatesName, id, RedisGeoCommands.DistanceUnit.KILOMETERS);
                //计算好距离后删除缓存中的用户经纬度
                redisTemplate.opsForGeo().remove(key, userCoordinatesName);
                //设置用户距离(用户当前位置和收费区域的距离)
                BigDecimal dis = new BigDecimal(userDistance.getValue() + "");

            }
        }


    }
}
