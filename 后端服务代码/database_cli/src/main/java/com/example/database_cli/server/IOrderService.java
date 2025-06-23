package com.example.database_cli.server;

import com.example.database_cli.model.entity.Order;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoOrder;

public interface IOrderService {
    /**
     * 创建订单
     */
    Result createOrder(VoOrder voorder);

    /**
     * 支付订单
     */
    Result payOrder(String orderId);

    // 查询相关
    Result selectByBuyerId(String buyerId);
    Result selectBySellerId(String sellerId);
    Result selectByGoodsId(String goodsId);
    Result selectByState(String state);
    Result selectAll();
    Result selectByTimeRange(String startTime, String endTime);
    Result selectByBuyerIdAndState(String buyerId, String state);
    Result selectBySellerIdAndState(String sellerId, String state);

    // 分页：根据买家ID查询订单
    Result selectByBuyerIdPaged(String buyerId, int page, int pageSize);
    // 分页：根据卖家ID查询订单
    Result selectBySellerIdPaged(String sellerId, int page, int pageSize);
    // 分页：根据买家ID和状态查询订单
    Result selectByBuyerIdAndStatePaged(String buyerId, String state, int page, int pageSize);
    // 分页：根据卖家ID和状态查询订单
    Result selectBySellerIdAndStatePaged(String sellerId, String state, int page, int pageSize);

    // 更新/删除相关
    Result deleteByBuyerIdAndSellerIdAndGoodsId(String buyerId, String sellerId, String goodsId);
    Result updateState(String buyerId, String sellerId, String goodsId, String state);
    
    /**
     * 处理超时订单：将待支付状态超过15分钟的订单转为取消，并将商品数量加回去
     */
    Result handleTimeoutOrders();

    /**
     * 取消订单，将待支付订单状态设为取消并恢复库存
     */
    Result cancelOrder(String orderId);
}
