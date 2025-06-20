package com.example.database_cli.config;

import com.example.database_cli.server.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {
    
    @Autowired
    private IOrderService orderService;
    
    /**
     * 每5分钟执行一次，处理超时订单
     * 将待支付状态超过15分钟的订单转为取消，并恢复商品库存
     */
    @Scheduled(fixedRate = 300000) // 5分钟 = 300000毫秒
    public void handleTimeoutOrders() {
        try {
            log.info("开始执行超时订单处理任务...");
            var result = orderService.handleTimeoutOrders();
            log.info("超时订单处理完成: {}", result.getMsg());
        } catch (Exception e) {
            log.error("处理超时订单时发生错误: ", e);
        }
    }
} 