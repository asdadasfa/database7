package com.example.database_cli.control;

import com.example.database_cli.model.entity.Buyer;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoBuyer;
import com.example.database_cli.server.IBuyerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/buyer")
public class buyerControl {

    @Autowired
    private IBuyerService buyerService;

    /**
     * 买家注册
     * @param buyer 买家信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody VoBuyer buyer) {
        log.info("买家注册请求: {}", buyer.getBuyerName());
        return buyerService.register(buyer);
    }

    /**
     * 买家登录
     * @param buyerName 买家名称
     * @param buyerPassword 买家密码
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestParam String buyerName, @RequestParam String buyerPassword) {
        log.info("买家登录请求: {}", buyerName);
        return buyerService.login(buyerName, buyerPassword);
    }

    /**
     * 买家注销
     * @param buyerId 买家ID
     * @return 注销结果
     */
    @PostMapping("/logout")
    public Result logout(@RequestParam String buyerId) {
        log.info("买家注销请求: {}", buyerId);
        return buyerService.logout(buyerId);
    }

    /**
     * 根据ID查询买家信息
     * @param buyerId 买家ID
     * @return 买家信息
     */
    @GetMapping("/{buyerId}")
    public Result getBuyerById(@PathVariable String buyerId) {
        log.info("查询买家信息: {}", buyerId);
        return buyerService.getBuyerById(buyerId);
    }

    /**
     * 更新买家信息
     * @param buyer 买家信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public Result updateBuyer(@RequestBody Buyer buyer) {
        log.info("更新买家信息: {}", buyer.getBuyerId());
        return buyerService.updateBuyer(buyer);
    }
}
