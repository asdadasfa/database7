package com.example.database_cli.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class VoCartList {
    private List<VoCart> cartItems;
    private double totalAmount;
    private int itemCount;
    
    public VoCartList() {}
    
    public VoCartList(List<VoCart> cartItems, double totalAmount, int itemCount) {
        this.cartItems = cartItems;
        this.totalAmount = totalAmount;
        this.itemCount = itemCount;
    }
} 