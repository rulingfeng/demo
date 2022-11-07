package com.example.demo.config.shard;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;


//sharding分表规则
//按单月分表
@Component
public class DateShardingAlgorithm implements StandardShardingAlgorithm<Long> {
    // 查询使用
    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Long> rangeShardingValue) {
        collection.forEach(i -> System.out.println("节点配置表名为: " + i));
        // 查询数据库中的表 hss_history
        List<String> tableNames = ShardingAlgorithmTool.getAllTableNameBySchema();
        tableNames.forEach(i -> System.out.println("数据库实时表名: " + i));
        // 查询缓存中的表 hss_history
        HashSet<String> tableNameCache = ShardingAlgorithmTool.cacheTableNames();
        tableNameCache.forEach(i -> System.out.println("缓存中的表名: " + i));
        // 获取查询条件 精确匹配表
        Range<Long> valueRange = rangeShardingValue.getValueRange();
        Long beginLong = valueRange.lowerEndpoint();// 开始条件的时候戳
        Long endLong = valueRange.upperEndpoint();// 结束条件的时候戳
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMM");
        String begin = sf.format(new Date(beginLong * 1000));
        String end = sf.format(new Date(endLong * 1000));
        // 不要匹配节点配置的表，数据库表一旦不存在就会报错
        List<String> queryTables = new ArrayList<>(tableNameCache.size());
        for (String tableName : tableNameCache) {
            if (!tableName.equals("order_main")) {
                // 截取缓存表名后缀的年月 yyyyMM
                String num = tableName.split("_")[2];
                // 在查询条件范围内才添加
                if (Integer.parseInt(num) >= Integer.parseInt(begin) && Integer.parseInt(num) <= Integer.parseInt(end)) {
                    queryTables.add(tableName);
                }
            }
        }
        // 返回按条件匹配的表名
        return queryTables;
    }

    // 添加使用
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Long> preciseShardingValue) {
        StringBuilder resultTableName = new StringBuilder();
        String logicTableName = preciseShardingValue.getLogicTableName();
        //表名精确匹配，表名加上截取的时间
        resultTableName.append(logicTableName)
                //时间戳秒级转毫秒级转成date类型
                .append("_").append(DateUtil.format(new Date(preciseShardingValue.getValue() * 1000), "yyyyMM"));
        System.out.println("插入表名为:" + resultTableName);
        return ShardingAlgorithmTool.shardingTablesCheckAndCreatAndReturn(logicTableName, resultTableName.toString());
    }

    @Override
    public void init() {

    }

    @Override
    public String getType() {
        //　自定义
        return null;
    }
}
