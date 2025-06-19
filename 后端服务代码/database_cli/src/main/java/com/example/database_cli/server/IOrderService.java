package com.example.database_cli.server;

import com.example.database_cli.model.entity.Order;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoOrder;

public interface IOrderService {
    /**
     * 创建订单
     * @param voorder 订单信息
     * @return 创建结果
     */
    Result createOrder(VoOrder voorder);
} 