package com.example.database_cli.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.database_cli.mapper")
public class MybatisConfig {
    // 这里可以添加自定义MyBatis配置Bean
}