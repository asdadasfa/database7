package com.example.database_cli.server;

import com.example.database_cli.model.result.Result;

public interface ICartService {
    
    /**
     * 添加商品到购物车
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @param num 数量
     * @return 添加结果
     */
    Result addToCart(String buyerId, String goodsId, int num);
    
    /**
     * 获取购物车内容（包含商品详细信息）
     * @param buyerId 买家ID
     * @return 购物车内容
     */
    Result getCartContents(String buyerId);
    
    /**
     * 更新购物车商品数量
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @param num 新数量
     * @return 更新结果
     */
    Result updateCartItem(String buyerId, String goodsId, int num);
    
    /**
     * 从购物车移除商品
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @return 移除结果
     */
    Result removeFromCart(String buyerId, String goodsId);
    
    /**
     * 清空购物车
     * @param buyerId 买家ID
     * @return 清空结果
     */
    Result clearCart(String buyerId);
    
    /**
     * 分页获取购物车内容
     * @param buyerId 买家ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 分页购物车内容
     */
    Result getCartContentsPaged(String buyerId, int page, int pageSize);
} 