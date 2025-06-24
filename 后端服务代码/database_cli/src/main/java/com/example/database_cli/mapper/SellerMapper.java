package com.example.database_cli.mapper;

import com.example.database_cli.model.entity.Seller;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SellerMapper {
    
    // 根据ID查询卖家
    Seller selectById(@Param("sellerId") String sellerId);
    
    // 查询所有卖家
    List<Seller> selectAll();
    
    // 插入卖家
    int insert(Seller seller);
    
    // 更新卖家信息
    int update(Seller seller);
    
    // 根据ID删除卖家
    int deleteById(@Param("sellerId") String sellerId);
    
    // 根据卖家名称查询
    Seller selectBySellerName(@Param("sellerName") String sellerName);
    
    // 根据卖家名称和密码查询
    Seller selectBySellerNameAndPassword(@Param("sellerName") String sellerName, @Param("sellerPassword") String sellerPassword);
    
    // 查询所有卖家（包括已删除的，用于管理员查看）
    List<Seller> selectAllForAdmin();
    
    // 根据ID查询卖家（包括已删除的，用于管理员查看）
    Seller selectByIdForAdmin(@Param("sellerId") String sellerId);
    
    // 查询所有卖家（分页）
    List<Seller> selectAllPaged(@Param("offset") int offset, @Param("limit") int limit);
    
    // 统计所有卖家数量
    int countAll();
} 