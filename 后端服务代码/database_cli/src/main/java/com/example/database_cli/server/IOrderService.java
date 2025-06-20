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

    // 更新/删除相关
    Result deleteByBuyerIdAndSellerIdAndGoodsId(String buyerId, String sellerId, String goodsId);
    Result updateState(String buyerId, String sellerId, String goodsId, String state);
}
