package com.example.database_cli.server;

import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.result.Result;

public interface IGoodsService {
    
    /**
     * 添加商品
     * @param goods 商品信息
     * @return 添加结果
     */
    Result addGoods(Goods goods);
    
    /**
     * 根据ID查询商品
     * @param goodsId 商品ID
     * @return 商品信息
     */
    Result getGoodsById(String goodsId);
    
    /**
     * 查询所有商品
     * @return 商品列表
     */
    Result getAllGoods();
    
    /**
     * 根据卖家ID查询商品
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    Result getGoodsBySellerId(String sellerId);
    
    /**
     * 根据商品类型查询
     * @param type 商品类型
     * @return 商品列表
     */
    Result getGoodsByType(String type);
    
    /**
     * 根据商品名称模糊查询
     * @param goodsName 商品名称
     * @return 商品列表
     */
    Result getGoodsByNameLike(String goodsName);
    
    /**
     * 根据价格范围查询
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 商品列表
     */
    Result getGoodsByPriceRange(double minPrice, double maxPrice);
    
    /**
     * 更新商品信息
     * @param goods 商品信息
     * @return 更新结果
     */
    Result updateGoods(Goods goods);
    
    /**
     * 更新商品库存
     * @param goodsId 商品ID
     * @param num 库存数量
     * @return 更新结果
     */
    Result updateGoodsStock(String goodsId, int num);
    
    /**
     * 删除商品
     * @param goodsId 商品ID
     * @return 删除结果
     */
    Result deleteGoods(String goodsId);
} 