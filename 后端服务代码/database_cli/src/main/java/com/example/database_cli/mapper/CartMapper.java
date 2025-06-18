package com.example.database_cli.mapper;

import com.example.database_cli.model.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartMapper {
    
    // 根据买家ID和商品ID查询购物车项
    Cart selectByBuyerIdAndGoodsId(@Param("buyerId") String buyerId, @Param("goodsId") String goodsId);
    
    // 根据买家ID查询购物车
    List<Cart> selectByBuyerId(@Param("buyerId") String buyerId);
    
    // 查询所有购物车项
    List<Cart> selectAll();
    
    // 插入购物车项
    int insert(Cart cart);
    
    // 更新购物车项数量
    int update(Cart cart);
    
    // 根据买家ID和商品ID删除购物车项
    int deleteByBuyerIdAndGoodsId(@Param("buyerId") String buyerId, @Param("goodsId") String goodsId);
    
    // 根据买家ID清空购物车
    int deleteByBuyerId(@Param("buyerId") String buyerId);
    
    // 更新购物车项数量
    int updateNum(@Param("buyerId") String buyerId, @Param("goodsId") String goodsId, @Param("num") int num);
} 