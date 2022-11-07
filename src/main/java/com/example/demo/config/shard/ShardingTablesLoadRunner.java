package com.example.demo.config.shard;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 项目启动后 读取已有分表 进行缓存
 */
@Slf4j
@Order(value = 1) // 数字越小 越先执行
@Component
public class ShardingTablesLoadRunner implements CommandLineRunner {
    @Override
    public void run(String... args) {
        ShardingAlgorithmTool.tableNameCacheReload();
    }
}
