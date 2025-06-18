package com.example.database_cli.mapper;

import com.example.database_cli.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    
    // 根据买家ID查询订单
    List<Order> selectByBuyerId(@Param("buyerId") String buyerId);
    
    // 根据卖家ID查询订单
    List<Order> selectBySellerId(@Param("sellerId") String sellerId);
    
    // 根据商品ID查询订单
    List<Order> selectByGoodsId(@Param("goodsId") String goodsId);
    
    // 根据订单状态查询
    List<Order> selectByState(@Param("state") String state);
    
    // 查询所有订单
    List<Order> selectAll();
    
    // 插入订单
    int insert(Order order);
    
    // 更新订单信息
    int update(Order order);
    
    // 根据买家ID、卖家ID、商品ID删除订单
    int deleteByBuyerIdAndSellerIdAndGoodsId(@Param("buyerId") String buyerId, 
                                            @Param("sellerId") String sellerId, 
                                            @Param("goodsId") String goodsId);
    
    // 更新订单状态
    int updateState(@Param("buyerId") String buyerId, 
                   @Param("sellerId") String sellerId, 
                   @Param("goodsId") String goodsId, 
                   @Param("state") String state);
    
    // 根据时间范围查询订单
    List<Order> selectByTimeRange(@Param("startTime") String startTime, @Param("endTime") String endTime);
    
    // 根据买家ID和状态查询订单
    List<Order> selectByBuyerIdAndState(@Param("buyerId") String buyerId, @Param("state") String state);
    
    // 根据卖家ID和状态查询订单
    List<Order> selectBySellerIdAndState(@Param("sellerId") String sellerId, @Param("state") String state);
} 