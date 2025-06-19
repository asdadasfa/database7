package com.example.database_cli;

import com.example.database_cli.model.vo.VoGoods;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
public class GoodsControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    public void testAddGoodsWithImages() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 创建模拟的图片文件
        MockMultipartFile imageFile1 = new MockMultipartFile(
            "images", 
            "test1.jpg", 
            "image/jpeg", 
            "test image content 1".getBytes()
        );

        MockMultipartFile imageFile2 = new MockMultipartFile(
            "images", 
            "test2.png", 
            "image/png", 
            "test image content 2".getBytes()
        );

        // 创建商品数据
        MockMultipartFile goodsData = new MockMultipartFile(
            "goodsName", 
            "", 
            "text/plain", 
            "测试商品".getBytes()
        );

        MockMultipartFile sellerId = new MockMultipartFile(
            "sellerId", 
            "", 
            "text/plain", 
            "S001".getBytes()
        );

        MockMultipartFile type = new MockMultipartFile(
            "type", 
            "", 
            "text/plain", 
            "电子产品".getBytes()
        );

        MockMultipartFile price = new MockMultipartFile(
            "price", 
            "", 
            "text/plain", 
            "99.99".getBytes()
        );

        MockMultipartFile num = new MockMultipartFile(
            "num", 
            "", 
            "text/plain", 
            "100".getBytes()
        );

        // 执行请求
        mockMvc.perform(multipart("/goods/addGoods")
                .file(imageFile1)
                .file(imageFile2)
                .file(goodsData)
                .file(sellerId)
                .file(type)
                .file(price)
                .file(num))
                .andExpect(status().isOk());
    }

    @Test
    public void testAddGoodsWithoutImages() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // 创建商品数据（不包含图片）
        MockMultipartFile goodsData = new MockMultipartFile(
            "goodsName", 
            "", 
            "text/plain", 
            "测试商品无图片".getBytes()
        );

        MockMultipartFile sellerId = new MockMultipartFile(
            "sellerId", 
            "", 
            "text/plain", 
            "S001".getBytes()
        );

        MockMultipartFile type = new MockMultipartFile(
            "type", 
            "", 
            "text/plain", 
            "电子产品".getBytes()
        );

        MockMultipartFile price = new MockMultipartFile(
            "price", 
            "", 
            "text/plain", 
            "99.99".getBytes()
        );

        MockMultipartFile num = new MockMultipartFile(
            "num", 
            "", 
            "text/plain", 
            "100".getBytes()
        );

        // 执行请求
        mockMvc.perform(multipart("/goods/addGoods")
                .file(goodsData)
                .file(sellerId)
                .file(type)
                .file(price)
                .file(num))
                .andExpect(status().isOk());
    }
} 