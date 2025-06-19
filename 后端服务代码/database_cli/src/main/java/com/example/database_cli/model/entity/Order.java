package com.example.database_cli.model.entity;

import lombok.Data;

@Data
public class Order {
    String orderId;
    String buyerId;
    String sellerId;
    String goodsId;
    String state;
    int  num;
    double sum;
    String time;
    boolean isBool=true;
}
