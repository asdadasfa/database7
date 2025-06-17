package com.example.database_cli.control;

import com.example.database_cli.model.entity.Seller;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.ISellerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/seller")
public class sellerConfig {

    @Autowired
    private ISellerService sellerService;

    /**
     * 卖家注册
     * @param seller 卖家信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody Seller seller) {
        log.info("卖家注册请求: {}", seller.getSellerName());
        return sellerService.register(seller);
    }

    /**
     * 卖家登录
     * @param sellerName 卖家名称
     * @param sellerPassword 卖家密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestParam String sellerName, @RequestParam String sellerPassword) {
        log.info("卖家登录请求: {}", sellerName);
        return sellerService.login(sellerName, sellerPassword);
    }

    /**
     * 卖家注销
     * @param sellerId 卖家ID
     * @return 注销结果
     */
    @PostMapping("/logout")
    public Result logout(@RequestParam String sellerId) {
        log.info("卖家注销请求: {}", sellerId);
        return sellerService.logout(sellerId);
    }

    /**
     * 根据ID查询卖家信息
     * @param sellerId 卖家ID
     * @return 卖家信息
     */
    @GetMapping("/{sellerId}")
    public Result getSellerById(@PathVariable String sellerId) {
        log.info("查询卖家信息: {}", sellerId);
        return sellerService.getSellerById(sellerId);
    }

    /**
     * 更新卖家信息
     * @param seller 卖家信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result updateSeller(@RequestBody Seller seller) {
        log.info("更新卖家信息: {}", seller.getSellerId());
        return sellerService.updateSeller(seller);
    }
}
