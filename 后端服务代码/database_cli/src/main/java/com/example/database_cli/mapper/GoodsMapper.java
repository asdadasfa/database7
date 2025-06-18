package com.example.database_cli.mapper;

import com.example.database_cli.model.entity.Goods;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GoodsMapper {
    
    // 根据ID查询商品
    Goods selectById(@Param("goodsId") String goodsId);
    
    // 查询所有商品
    List<Goods> selectAll();
    
    // 插入商品
    int insert(Goods goods);
    
    // 更新商品信息
    int update(Goods goods);
    
    // 根据ID删除商品
    int deleteById(@Param("goodsId") String goodsId);
    
    // 根据卖家ID查询商品
    List<Goods> selectBySellerId(@Param("sellerId") String sellerId);
    
    // 根据商品类型查询
    List<Goods> selectByType(@Param("type") String type);
    
    // 根据商品名称模糊查询
    List<Goods> selectByGoodsNameLike(@Param("goodsName") String goodsName);
    
    // 根据价格范围查询
    List<Goods> selectByPriceRange(@Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);
    
    // 更新商品库存
    int updateNum(@Param("goodsId") String goodsId, @Param("num") int num);
} 