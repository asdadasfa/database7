package com.example.database_cli.model.vo;

import lombok.Data;

@Data
public class VoOrder {
    String buyerId;
    String sellerId;
    String goodsId;
    int  num;
}
