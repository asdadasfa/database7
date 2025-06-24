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
import java.time.Duration;
import java.util.UUID;
import java.util.List;

@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    @Transactional
    public Result createOrder(VoOrder voorder) {
        // 查询商品（加悲观锁）
        Goods goods = goodsMapper.selectByIdForUpdate(voorder.getGoodsId());
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

    /**
     * 恢复商品库存
     */
    private void restoreGoodsStock(Order order) {
        // 加悲观锁
        Goods goods = goodsMapper.selectByIdForUpdate(order.getGoodsId());
        if (goods != null) {
            goodsMapper.updateNum(goods.getGoodsId(), goods.getNum() + order.getNum());
        }
    }

    @Override
    @Transactional
    public Result payOrder(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.isBool()) {
            return Result.fail("订单不存在或已被删除");
        }
        if (!"待支付".equals(order.getState())) {
            return Result.fail("订单状态不是待支付，无法支付");
        }
        // 检查时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime orderTime = LocalDateTime.parse(order.getTime(), formatter);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(orderTime, now);
        if (duration.toMinutes() > 15) {
            // 超时，设为取消
            order.setState("取消");
            orderMapper.update(order);
            restoreGoodsStock(order);
            return Result.fail("订单已超时，已取消");
        } else {
            // 支付成功
            order.setState("支付成功");
            order.setTime(now.format(formatter));
            orderMapper.update(order);
            return Result.success("支付成功");
        }
    }

    @Override
    public Result selectByBuyerId(String buyerId) {
        return Result.success(orderMapper.selectByBuyerId(buyerId));
    }

    @Override
    public Result selectBySellerId(String sellerId) {
        return Result.success(orderMapper.selectBySellerId(sellerId));
    }

    @Override
    public Result selectByGoodsId(String goodsId) {
        return Result.success(orderMapper.selectByGoodsId(goodsId));
    }

    @Override
    public Result selectByState(String state) {
        return Result.success(orderMapper.selectByState(state));
    }

    @Override
    public Result selectAll() {
        return Result.success(orderMapper.selectAll());
    }

    @Override
    @Transactional
    public Result deleteByBuyerIdAndSellerIdAndGoodsId(String buyerId, String sellerId, String goodsId) {
        int res = orderMapper.deleteByBuyerIdAndSellerIdAndGoodsId(buyerId, sellerId, goodsId);
        if (res > 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败");
        }
    }

    @Override
    @Transactional
    public Result updateState(String buyerId, String sellerId, String goodsId, String state) {
        int res = orderMapper.updateState(buyerId, sellerId, goodsId, state);
        if (res > 0) {
            return Result.success("状态更新成功");
        } else {
            return Result.fail("状态更新失败");
        }
    }

    @Override
    public Result selectByTimeRange(String startTime, String endTime) {
        return Result.success(orderMapper.selectByTimeRange(startTime, endTime));
    }

    @Override
    public Result selectByBuyerIdAndState(String buyerId, String state) {
        return Result.success(orderMapper.selectByBuyerIdAndState(buyerId, state));
    }

    @Override
    public Result selectBySellerIdAndState(String sellerId, String state) {
        return Result.success(orderMapper.selectBySellerIdAndState(sellerId, state));
    }

    /**
     * 处理超时订单：将待支付状态超过15分钟的订单转为取消，并将商品数量加回去
     */
    @Override
    @Transactional
    public Result handleTimeoutOrders() {
        try {
            // 获取所有待支付的订单
            List<Order> pendingOrders = orderMapper.selectByState("待支付");
            if (pendingOrders.isEmpty()) {
                return Result.success("没有待处理的超时订单");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime now = LocalDateTime.now();
            int processedCount = 0;
            int restoredCount = 0;

            for (Order order : pendingOrders) {
                LocalDateTime orderTime = LocalDateTime.parse(order.getTime(), formatter);
                Duration duration = Duration.between(orderTime, now);
                
                // 检查是否超过15分钟
                if (duration.toMinutes() > 15) {
                    // 更新订单状态为取消
                    order.setState("取消");
                    int updateResult = orderMapper.update(order);
                    
                    if (updateResult > 0) {
                        processedCount++;
                        restoreGoodsStock(order);
                        restoredCount++;
                    }
                }
            }

            return Result.success(String.format("处理完成：%d个订单已取消，%d个商品库存已恢复", processedCount, restoredCount));
        } catch (Exception e) {
            return Result.fail("处理超时订单失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result cancelOrder(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.isBool()) {
            return Result.fail("订单不存在或已被删除");
        }
        if (!"待支付".equals(order.getState())) {
            return Result.fail("只有待支付订单才能取消");
        }
        order.setState("取消");
        int updateRes = orderMapper.update(order);
        if (updateRes > 0) {
            restoreGoodsStock(order);
            return Result.success("订单已取消，库存已恢复");
        } else {
            return Result.fail("订单取消失败");
        }
    }

    // 分页：根据买家ID查询订单
    @Override
    public Result selectByBuyerIdPaged(String buyerId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return Result.success(orderMapper.selectByBuyerIdPaged(buyerId, offset, pageSize));
    }
    // 分页：根据卖家ID查询订单
    @Override
    public Result selectBySellerIdPaged(String sellerId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return Result.success(orderMapper.selectBySellerIdPaged(sellerId, offset, pageSize));
    }
    // 分页：根据买家ID和状态查询订单
    @Override
    public Result selectByBuyerIdAndStatePaged(String buyerId, String state, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return Result.success(orderMapper.selectByBuyerIdAndStatePaged(buyerId, state, offset, pageSize));
    }
    // 分页：根据卖家ID和状态查询订单
    @Override
    public Result selectBySellerIdAndStatePaged(String sellerId, String state, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return Result.success(orderMapper.selectBySellerIdAndStatePaged(sellerId, state, offset, pageSize));
    }
} 