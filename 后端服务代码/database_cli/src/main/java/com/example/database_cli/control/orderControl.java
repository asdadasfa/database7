package com.example.database_cli.control;

import com.example.database_cli.model.entity.Order;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoOrder;
import com.example.database_cli.server.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/order")
public class orderControl {
    @Autowired
    private IOrderService orderService;

    /**
     * 创建订单接口
     */
    @PostMapping("/create")
    public Result createOrder(@RequestBody VoOrder voorder) {
        return orderService.createOrder(voorder);
    }

    /**
     * 支付订单接口
     */
    @PostMapping("/pay")
    public Result payOrder(@RequestParam String orderId) {
        return orderService.payOrder(orderId);
    }

    /**
     * 处理超时订单接口
     */
    @PostMapping("/handle-timeout")
    public Result handleTimeoutOrders() {
        return orderService.handleTimeoutOrders();
    }

    /**
     * 取消订单接口
     */
    @PostMapping("/cancel")
    public Result cancelOrder(@RequestParam String orderId) {
        return orderService.cancelOrder(orderId);
    }

    /**
     * 获取待支付订单
     */
    @GetMapping("/pending")
    public Result getPendingOrders(@RequestParam String buyerId) {
        return orderService.selectByBuyerIdAndState(buyerId, "待支付");
    }

    /**
     * 获取已支付订单
     */
    @GetMapping("/paid")
    public Result getPaidOrders(@RequestParam String buyerId) {
        return orderService.selectByBuyerIdAndState(buyerId, "支付成功");
    }

    /**
     * 获取已取消订单
     */
    @GetMapping("/cancelled")
    public Result getCancelledOrders(@RequestParam String buyerId) {
        return orderService.selectByBuyerIdAndState(buyerId, "取消");
    }
}
