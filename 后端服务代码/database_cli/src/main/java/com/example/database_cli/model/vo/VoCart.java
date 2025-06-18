package com.example.database_cli.model.vo;

import lombok.Data;

@Data
public class VoCart {
    String buyerId;
    String goodsId;
    String sellerId;
    String goodsName;
    String type;
    double price;
    int num;
    double sum;
    byte[] goodsImage;
}
