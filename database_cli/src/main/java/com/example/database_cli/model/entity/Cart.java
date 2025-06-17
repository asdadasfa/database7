package com.example.database_cli.model.entity;

import lombok.Data;

@Data
public class Cart {
    String buyerId;
    String goodsId;
    int num;
}
