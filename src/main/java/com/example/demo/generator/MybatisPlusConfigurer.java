//
//
//package com.example.demo.generator;
//
//import org.apache.ibatis.session.SqlSessionFactory;
//import org.mybatis.spring.annotation.MapperScan;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.AutoConfigureAfter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//
///**
// * @Desc 加载配置文件
// */
//@Configuration
//@MapperScan("com.example.demo.dao")
//
////@AutoConfigureAfter(PageHelperAutoConfiguration.class)
//public class MybatisPlusConfigurer {
////    @Autowired
////    private List<SqlSessionFactory> sqlSessionFactoryList;
////
////    @PostConstruct
////    public void addMyInterceptor() {
////        MyBatisInterceptor e = new MyBatisInterceptor();
////        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
////            sqlSessionFactory.getConfiguration().addInterceptor(e);
////        }
////    }
//}
