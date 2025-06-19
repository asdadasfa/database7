package com.example.database_cli.model.entity;

import lombok.Data;

import java.util.List;

@Data
public class Goods {
    String goodsId;
    String goodsName;
    String sellerId;
    String type;
    double price;
    int num;
    List<String> images;
}
