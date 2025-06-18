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
     * 创建订单接口
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @param num 购买数量
     * @return 创建结果
     */
    @PostMapping("/createOrder")
    public Result<String> createOrder(@RequestParam String buyerId,
                                     @RequestParam String goodsId,
                                     @RequestParam int num) {
        try {
            // 1. 验证商品是否存在
            Goods goods = goodsMapper.selectById(goodsId);
            if (goods == null) {
                return Result.fail();
            }
            
            // 2. 验证库存是否充足
            if (goods.getNum() < num) {
                return Result.fail();
            }
            
            // 3. 计算订单总金额
            double totalSum = goods.getPrice() * num;
            
            // 4. 创建订单对象
            Order order = new Order();
            order.setBuyerId(buyerId);
            order.setSellerId(goods.getSellerId());
            order.setGoodsId(goodsId);
            order.setState("待付款"); // 初始状态
            order.setNum(num);
            order.setSum(totalSum);
            order.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            
            // 5. 保存订单
            int result = orderMapper.insert(order);
            if (result > 0) {
                // 6. 更新商品库存
                int newStock = goods.getNum() - num;
                goodsMapper.updateNum(goodsId, newStock);
                
                log.info("订单创建成功: 买家ID={}, 商品ID={}, 数量={}, 总金额={}", 
                        buyerId, goodsId, num, totalSum);
                return Result.success("订单创建成功");
            } else {
                log.error("订单创建失败: 数据库插入失败");
                return Result.fail();
            }
            
        } catch (Exception e) {
            log.error("创建订单时发生异常: ", e);
            return Result.fail();
        }
    }

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
