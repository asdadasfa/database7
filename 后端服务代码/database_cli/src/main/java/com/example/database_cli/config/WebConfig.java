package com.example.database_cli.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);
    private static final String IMAGE_UPLOAD_PATH = "uploaded_images";

    public WebConfig() {
        System.out.println("WebConfig 构造方法被调用");
    }

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
        // 获取当前 module (database_cli) 的绝对路径
        String basePath = System.getProperty("user.dir");
        String imagePath = Paths.get(basePath, "uploaded_images").toString();
        File dir = new File(imagePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + imagePath + File.separator);
        System.out.println("图片物理路径：" + imagePath);
    }

    private List<String> saveImages(List<MultipartFile> imageFiles) {
        List<String> imagePaths = new ArrayList<>();
        if (imageFiles == null || imageFiles.isEmpty()) {
            return imagePaths;
        }
        File uploadDir = new File(IMAGE_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        for (MultipartFile imageFile : imageFiles) {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String originalFilename = imageFile.getOriginalFilename();
                    String fileExtension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String fileName = "goods_" + UUID.randomUUID().toString().replace("-", "") + fileExtension;
                    File destFile = new File(uploadDir, fileName);
                    imageFile.transferTo(destFile);
                    imagePaths.add("http://localhost:8686/images/" + fileName);
                } catch (IOException e) {
                    e.printStackTrace();
                    // 错误处理
                }
            }
        }
        return imagePaths;
    }
}