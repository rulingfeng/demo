package com.example.demo.config.shard;

import com.example.demo.dao.MainOrderTwoMapper;
import com.example.demo.utils.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
public class ShardingAlgorithmToolTwo {
    private static final HashSet<String> tableNameCache = new HashSet<>();

    private static MainOrderTwoMapper orderMaionTwoMapper= SpringUtil.getBean(MainOrderTwoMapper.class);

    /**
     * 判断 分表获取的表名是否存在 不存在则自动建表
     *
     * @param logicTableName  逻辑表名(表头)
     * @param resultTableName 真实表名
     * @return 确认存在于数据库中的真实表名
     */
    public static String shardingTablesCheckAndCreatAndReturn(String logicTableName, String resultTableName) {
        synchronized (logicTableName.intern()) {
            // 缓存中有此表 返回
            if (tableNameCache.contains(resultTableName)) {
                return resultTableName;
            }
            // 缓存中无此表 建表 并添加缓存
            // 调用mapper 创建表
            // @Update("CREATE TABLE IF NOT EXISTS ${name} LIKE hss_history")
            orderMaionTwoMapper.createTable(resultTableName);
            tableNameCache.add(resultTableName);
        }
        return resultTableName;
    }

    /**
     * 缓存重载方法
     */
    public static void tableNameCacheReload() {
        // 读取数据库中所有表名
        List<String> tableNameList = getAllTableNameBySchema();
        // 删除旧的缓存(如果存在)
        ShardingAlgorithmToolTwo.tableNameCache.clear();
        // 写入新的缓存
        ShardingAlgorithmToolTwo.tableNameCache.addAll(tableNameList);
    }

    /**
     * 获取数据库中的表名
     */
    public static List<String> getAllTableNameBySchema() {
        List<String> res = new ArrayList<>();
        // 获取数据中的表名，需要自己构建数据源 SHOW TABLES like 'hss_history%'
        // List<String> tableNames = hssHistoryMapper.showTables();
        Environment env = SpringUtil.getApplicationContext().getEnvironment();
        try (Connection connection = DriverManager.getConnection(env.getProperty("spring.datasource.druid.url"), env.getProperty("spring.datasource.druid.username"), env.getProperty("spring.datasource.druid.password"));
             Statement st = connection.createStatement()) {
            try (ResultSet rs = st.executeQuery("SHOW TABLES like 'main_order_two%'")) {
                while (rs.next()) {
                    res.add(rs.getString(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取缓存中的表名
     * @return
     */
    public static HashSet<String> cacheTableNames() {
        return tableNameCache;
    }
}
