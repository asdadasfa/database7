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
     * 获取待支付订单（分页）
     */
    @GetMapping("/pending")
    public Result getPendingOrders(@RequestParam String buyerId,
                                   @RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectByBuyerIdAndStatePaged(buyerId, "待支付", page, pageSize);
    }

    /**
     * 获取已支付订单（分页）
     */
    @GetMapping("/paid")
    public Result getPaidOrders(@RequestParam String buyerId,
                                @RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectByBuyerIdAndStatePaged(buyerId, "支付成功", page, pageSize);
    }

    /**
     * 获取已取消订单（分页）
     */
    @GetMapping("/cancelled")
    public Result getCancelledOrders(@RequestParam String buyerId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectByBuyerIdAndStatePaged(buyerId, "取消", page, pageSize);
    }

    /**
     * 根据buyerId获取所有订单
     * @param buyerId
     * @return
     */
    @GetMapping("/by-buyer")
    public Result getOrdersByBuyerId(@RequestParam String buyerId) {
        return orderService.selectByBuyerId(buyerId);
    }

    /**
     * 根据sellerId获取所有订
     * @param sellerId
     * @return
     */
    @GetMapping("/by-seller")
    public Result getOrdersBySellerId(@RequestParam String sellerId) {
        return orderService.selectBySellerId(sellerId);
    }

    /**
     * 分页：根据buyerId获取订单
     */
    @GetMapping("/by-buyer-paged")
    public Result getOrdersByBuyerIdPaged(@RequestParam String buyerId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectByBuyerIdPaged(buyerId, page, pageSize);
    }

    /**
     * 分页：根据sellerId获取订单
     */
    @GetMapping("/by-seller-paged")
    public Result getOrdersBySellerIdPaged(@RequestParam String sellerId,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectBySellerIdPaged(sellerId, page, pageSize);
    }

    /**
     * 分页：根据buyerId和状态获取订单
     */
    @GetMapping("/by-buyer-state-paged")
    public Result getOrdersByBuyerIdAndStatePaged(@RequestParam String buyerId,
                                                  @RequestParam String state,
                                                  @RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectByBuyerIdAndStatePaged(buyerId, state, page, pageSize);
    }

    /**
     * 分页：根据sellerId和状态获取订单
     */
    @GetMapping("/by-seller-state-paged")
    public Result getOrdersBySellerIdAndStatePaged(@RequestParam String sellerId,
                                                   @RequestParam String state,
                                                   @RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(defaultValue = "5") int pageSize) {
        return orderService.selectBySellerIdAndStatePaged(sellerId, state, page, pageSize);
    }
}
