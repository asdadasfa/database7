package com.example.database_cli.mapper;

import com.example.database_cli.model.entity.Buyer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BuyerMapper {
    
    // 根据ID查询买家
    Buyer selectById(@Param("buyerId") String buyerId);
    
    // 查询所有买家
    List<Buyer> selectAll();
    
    // 插入买家
    int insert(Buyer buyer);
    
    // 更新买家信息
    int update(Buyer buyer);
    
    // 根据ID删除买家
    int deleteById(@Param("buyerId") String buyerId);
    
    // 根据用户名查询买家
    Buyer selectByBuyerName(@Param("buyerName") String buyerName);
} 