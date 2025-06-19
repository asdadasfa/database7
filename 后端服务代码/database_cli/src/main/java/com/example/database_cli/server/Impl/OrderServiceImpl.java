package com.example.database_cli.server.Impl;

import com.example.database_cli.mapper.GoodsMapper;
import com.example.database_cli.mapper.OrderMapper;
import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.entity.Order;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoOrder;
import com.example.database_cli.server.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    @Transactional
    public Result createOrder(VoOrder voorder) {
        // 查询商品
        Goods goods = goodsMapper.selectById(voorder.getGoodsId());
        if (goods == null || !goods.isBool() || goods.getNum() < voorder.getNum()) {
            return Result.fail("商品不存在或库存不足");
        }
        // 扣减库存
        int newNum = goods.getNum() - voorder.getNum();
        int updateRes = goodsMapper.updateNum(goods.getGoodsId(), newNum);
        if (updateRes <= 0) {
            return Result.fail("扣减库存失败");
        }
        // 设置订单状态、时间、主键
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        order.setBuyerId(voorder.getBuyerId());
        order.setSellerId(voorder.getSellerId());
        order.setGoodsId(voorder.getGoodsId());
        order.setNum(voorder.getNum());
        order.setState("待支付");
        order.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        order.setBool(true);
        // 计算总价
        order.setSum(goods.getPrice() * order.getNum());
        // 插入订单
        int insertRes = orderMapper.insert(order);
        if (insertRes > 0) {
            return Result.success(order);
        } else {
            return Result.fail("订单创建失败");
        }
    }
} 