package com.example.database_cli.model.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class VoGoods {
    String goodsName;
    String sellerId;
    String type;
    double price;
    int num;
    List<MultipartFile> images;
}
