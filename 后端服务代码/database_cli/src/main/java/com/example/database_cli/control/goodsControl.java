package com.example.database_cli.control;

import com.example.database_cli.mapper.GoodsMapper;
import com.example.database_cli.mapper.OrderMapper;
import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.entity.Order;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@Slf4j
@RequestMapping("/goods")
public class goodsControl {
    
    @Autowired
    private GoodsMapper goodsMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private IGoodsService goodsService;
    

    /**
     * 创建商品接口（使用服务层）
     * @param goods 商品信息
     * @return 创建结果
     */
    @PostMapping("/addGoods")
    public Result<String> addGoods(@RequestBody Goods goods) {
        try {
            Result result = goodsService.addGoods(goods);
            if (result.getCode() == 200) {
                log.info("商品创建成功: 商品ID={}, 商品名称={}", 
                        goods.getGoodsId(), goods.getGoodsName());
            } else {
                log.error("商品创建失败: {}", result.getMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("创建商品时发生异常: ", e);
            return Result.fail();
        }
    }
    
    /**
     * 查询所有商品
     * @return 商品列表
     */
    @GetMapping("/getAllGoods")
    public Result getAllGoods() {
        return goodsService.getAllGoods();
    }
    
    /**
     * 根据ID查询商品
     * @param goodsId 商品ID
     * @return 商品信息
     */
    @GetMapping("/getGoodsById")
    public Result getGoodsById(@RequestParam String goodsId) {
        return goodsService.getGoodsById(goodsId);
    }
    
    /**
     * 根据卖家ID查询商品
     * @param sellerId 卖家ID
     * @return 商品列表
     */
    @GetMapping("/getGoodsBySellerId")
    public Result getGoodsBySellerId(@RequestParam String sellerId) {
        return goodsService.getGoodsBySellerId(sellerId);
    }
    
    /**
     * 根据商品类型查询
     * @param type 商品类型
     * @return 商品列表
     */
    @GetMapping("/getGoodsByType")
    public Result getGoodsByType(@RequestParam String type) {
        return goodsService.getGoodsByType(type);
    }
    
    /**
     * 根据商品名称模糊查询
     * @param goodsName 商品名称
     * @return 商品列表
     */
    @GetMapping("/getGoodsByNameLike")
    public Result getGoodsByNameLike(@RequestParam String goodsName) {
        return goodsService.getGoodsByNameLike(goodsName);
    }
    
    /**
     * 根据价格范围查询
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @return 商品列表
     */
    @GetMapping("/getGoodsByPriceRange")
    public Result getGoodsByPriceRange(@RequestParam double minPrice, 
                                      @RequestParam double maxPrice) {
        return goodsService.getGoodsByPriceRange(minPrice, maxPrice);
    }
    
    /**
     * 更新商品信息
     * @param goods 商品信息
     * @return 更新结果
     */
    @PutMapping("/updateGoods")
    public Result updateGoods(@RequestBody Goods goods) {
        return goodsService.updateGoods(goods);
    }
    
    /**
     * 更新商品库存
     * @param goodsId 商品ID
     * @param num 库存数量
     * @return 更新结果
     */
    @PutMapping("/updateGoodsStock")
    public Result updateGoodsStock(@RequestParam String goodsId, 
                                  @RequestParam int num) {
        return goodsService.updateGoodsStock(goodsId, num);
    }
    
    /**
     * 删除商品
     * @param goodsId 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/deleteGoods")
    public Result deleteGoods(@RequestParam String goodsId) {
        return goodsService.deleteGoods(goodsId);
    }
}
