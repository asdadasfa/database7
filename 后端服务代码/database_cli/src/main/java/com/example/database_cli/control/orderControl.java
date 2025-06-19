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
}
