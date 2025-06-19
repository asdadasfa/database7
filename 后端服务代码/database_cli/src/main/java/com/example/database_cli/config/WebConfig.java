package com.example.database_cli.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 先设置映射
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 允许向该服务器提交请求的URI，*表示全部允许，自定义可以添加多个，在SpringMVC中，如果设成*，会自动转成当前请求头中的Origin
                .allowCredentials(true) // 允许cookies跨域
                .allowedHeaders("*") // 请求头字段
                .allowedMethods("GET", "POST", "DELETE", "PUT") // 允许跨域的方法
                .maxAge(3600);// 预检请求的缓存时间（秒），即在这个时间段里，对于相同的跨域请求不会再预检了
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 让 /images/** 映射到本地 images 目录
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:src/main/java/com/example/database_cli/images/");
    }
}