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
    
    // 查询所有买家（包括已删除的，用于管理员查看）
    List<Buyer> selectAllForAdmin();
    
    // 根据ID查询买家（包括已删除的，用于管理员查看）
    Buyer selectByIdForAdmin(@Param("buyerId") String buyerId);
    
    // 查询所有买家（分页）
    List<Buyer> selectAllPaged(@Param("offset") int offset, @Param("limit") int limit);
    
    // 统计所有买家数量
    int countAll();
} 