package com.example.database_cli.control;

import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoCartList;
import com.example.database_cli.server.ICartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/cart")
public class cartControl {
    
    @Autowired
    private ICartService cartService;
    
    /**
     * 添加商品到购物车
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @param num 数量
     * @return 添加结果
     */
    @PostMapping("/addToCart")
    public Result addToCart(@RequestParam String buyerId,
                           @RequestParam String goodsId,
                           @RequestParam int num) {
        try {
            log.info("添加商品到购物车: 买家ID={}, 商品ID={}, 数量={}", buyerId, goodsId, num);
            Result result = cartService.addToCart(buyerId, goodsId, num);
            if (result.getCode() != 200) {
                log.warn("添加商品到购物车失败: {}", result.getMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("添加商品到购物车时发生异常: ", e);
            return Result.fail();
        }
    }
    
    /**
     * 获取购物车内容
     * @param buyerId 买家ID
     * @return 购物车内容（包含商品详细信息）
     */
    @GetMapping("/getCartContents")
    public Result getCartContents(@RequestParam String buyerId) {
        try {
            log.info("获取购物车内容: 买家ID={}", buyerId);
            Result result = cartService.getCartContents(buyerId);
            if (result.getCode() == 200) {
                VoCartList cartList = (VoCartList) result.getData();
                log.info("成功获取购物车内容，商品数量: {}", cartList != null ? cartList.getItemCount() : 0);
            } else {
                log.warn("获取购物车内容失败: {}", result.getMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("获取购物车内容时发生异常: ", e);
            return Result.fail();
        }
    }
    
    /**
     * 更新购物车商品数量
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @param num 新数量
     * @return 更新结果
     */
    @PutMapping("/updateCartItem")
    public Result updateCartItem(@RequestParam String buyerId,
                                @RequestParam String goodsId,
                                @RequestParam int num) {
        try {
            log.info("更新购物车商品数量: 买家ID={}, 商品ID={}, 新数量={}", buyerId, goodsId, num);
            Result result = cartService.updateCartItem(buyerId, goodsId, num);
            if (result.getCode() != 200) {
                log.warn("更新购物车商品数量失败: {}", result.getMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("更新购物车商品数量时发生异常: ", e);
            return Result.fail();
        }
    }
    
    /**
     * 从购物车移除商品
     * @param buyerId 买家ID
     * @param goodsId 商品ID
     * @return 移除结果
     */
    @DeleteMapping("/removeFromCart")
    public Result removeFromCart(@RequestParam String buyerId,
                                @RequestParam String goodsId) {
        try {
            log.info("从购物车移除商品: 买家ID={}, 商品ID={}", buyerId, goodsId);
            Result result = cartService.removeFromCart(buyerId, goodsId);
            if (result.getCode() != 200) {
                log.warn("从购物车移除商品失败: {}", result.getMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("从购物车移除商品时发生异常: ", e);
            return Result.fail();
        }
    }
    
    /**
     * 清空购物车
     * @param buyerId 买家ID
     * @return 清空结果
     */
    @DeleteMapping("/clearCart")
    public Result clearCart(@RequestParam String buyerId) {
        try {
            log.info("清空购物车: 买家ID={}", buyerId);
            Result result = cartService.clearCart(buyerId);
            if (result.getCode() != 200) {
                log.warn("清空购物车失败: {}", result.getMsg());
            }
            return result;
        } catch (Exception e) {
            log.error("清空购物车时发生异常: ", e);
            return Result.fail();
        }
    }
}
