package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.GoodsMapper;
import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Result addGoods(Goods goods) {
        // 参数校验
        if (goods == null || !StringUtils.hasText(goods.getGoodsName()) 
            || !StringUtils.hasText(goods.getSellerId())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        if (goods.getPrice() <= 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        if (goods.getNum() < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 生成商品ID
        goods.setGoodsId("G" + UUID.randomUUID().toString().replace("-", ""));
        
        // 保存商品信息
        int result = goodsMapper.insert(goods);
        if (result > 0) {
            return Result.success(goods);
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getGoodsById(String goodsId) {
        // 参数校验
        if (!StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询商品信息
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        return Result.success(goods);
    }

    @Override
    public Result getAllGoods() {
        // 查询所有商品
        List<Goods> goodsList = goodsMapper.selectAll();
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsBySellerId(String sellerId) {
        // 参数校验
        if (!StringUtils.hasText(sellerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询卖家商品
        List<Goods> goodsList = goodsMapper.selectBySellerId(sellerId);
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsByType(String type) {
        // 参数校验
        if (!StringUtils.hasText(type)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 根据类型查询商品
        List<Goods> goodsList = goodsMapper.selectByType(type);
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsByNameLike(String goodsName) {
        // 参数校验
        if (!StringUtils.hasText(goodsName)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 根据名称模糊查询商品
        List<Goods> goodsList = goodsMapper.selectByGoodsNameLike(goodsName);
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsByPriceRange(double minPrice, double maxPrice) {
        // 参数校验
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 根据价格范围查询商品
        List<Goods> goodsList = goodsMapper.selectByPriceRange(minPrice, maxPrice);
        return Result.success(goodsList);
    }

    @Override
    public Result updateGoods(Goods goods) {
        // 参数校验
        if (goods == null || !StringUtils.hasText(goods.getGoodsId())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查商品是否存在
        Goods existGoods = goodsMapper.selectById(goods.getGoodsId());
        if (existGoods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 价格校验
        if (goods.getPrice() != 0 && goods.getPrice() <= 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 库存校验
        if (goods.getNum() < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 更新商品信息
        int result = goodsMapper.update(goods);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result updateGoodsStock(String goodsId, int num) {
        // 参数校验
        if (!StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        if (num < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查商品是否存在
        Goods existGoods = goodsMapper.selectById(goodsId);
        if (existGoods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 更新商品库存
        int result = goodsMapper.updateNum(goodsId, num);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result deleteGoods(String goodsId) {
        // 参数校验
        if (!StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查商品是否存在
        Goods existGoods = goodsMapper.selectById(goodsId);
        if (existGoods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 删除商品
        int result = goodsMapper.deleteById(goodsId);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }
} 