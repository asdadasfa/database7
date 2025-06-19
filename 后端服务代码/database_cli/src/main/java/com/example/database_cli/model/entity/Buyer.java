package com.example.database_cli.model.entity;

import lombok.Data;

@Data
public class Buyer {
    String buyerId; // 买家ID
    String buyerName; // 买家名称
    String buyerPassword; // 买家密码
    boolean isBool=true;
}
