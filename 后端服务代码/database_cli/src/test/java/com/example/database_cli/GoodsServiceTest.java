package com.example.database_cli;

import com.example.database_cli.model.vo.VoGoods;
import com.example.database_cli.server.IGoodsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class GoodsServiceTest {

    @Autowired
    private IGoodsService goodsService;

    @Test
    public void testAddGoodsWithImages() {
        // 创建测试数据
        VoGoods voGoods = new VoGoods();
        voGoods.setGoodsName("测试商品");
        voGoods.setSellerId("S001");
        voGoods.setType("电子产品");
        voGoods.setPrice(99.99);
        voGoods.setNum(100);

        // 创建模拟图片文件
        List<MockMultipartFile> imageFiles = new ArrayList<>();
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
        imageFiles.add(imageFile1);
        imageFiles.add(imageFile2);
        voGoods.setImages(imageFiles);

        // 测试添加商品
        var result = goodsService.addGoodsWithImages(voGoods);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testAddGoodsWithoutImages() {
        // 创建测试数据（不包含图片）
        VoGoods voGoods = new VoGoods();
        voGoods.setGoodsName("测试商品无图片");
        voGoods.setSellerId("S001");
        voGoods.setType("电子产品");
        voGoods.setPrice(99.99);
        voGoods.setNum(100);

        // 测试添加商品
        var result = goodsService.addGoodsWithImages(voGoods);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
    }

    @Test
    public void testAddGoodsWithInvalidData() {
        // 测试空数据
        var result1 = goodsService.addGoodsWithImages(null);
        assertNotNull(result1);
        assertNotEquals(200, result1.getCode());

        // 测试缺少必要字段
        VoGoods voGoods = new VoGoods();
        voGoods.setGoodsName(""); // 空商品名称
        voGoods.setSellerId("S001");
        voGoods.setPrice(99.99);
        voGoods.setNum(100);

        var result2 = goodsService.addGoodsWithImages(voGoods);
        assertNotNull(result2);
        assertNotEquals(200, result2.getCode());

        // 测试无效价格
        VoGoods voGoods2 = new VoGoods();
        voGoods2.setGoodsName("测试商品");
        voGoods2.setSellerId("S001");
        voGoods2.setPrice(-10.0); // 负价格
        voGoods2.setNum(100);

        var result3 = goodsService.addGoodsWithImages(voGoods2);
        assertNotNull(result3);
        assertNotEquals(200, result3.getCode());
    }
} 