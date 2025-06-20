-- 处理超时订单的SQL脚本
-- 将待支付状态超过15分钟的订单转为取消，并将商品数量加回去

-- 1. 创建临时表存储需要处理的超时订单
CREATE TEMPORARY TABLE IF NOT EXISTS timeout_orders AS
SELECT 
    o.buyer_id,
    o.seller_id,
    o.goods_id,
    o.num,
    o.time
FROM `order` o
WHERE o.state = '待支付' 
  AND o.is_bool = 1
  AND TIMESTAMPDIFF(MINUTE, STR_TO_DATE(o.time, '%Y-%m-%d %H:%i:%s'), NOW()) > 15;

-- 2. 更新超时订单状态为取消
UPDATE `order` o
INNER JOIN timeout_orders t ON o.buyer_id = t.buyer_id 
    AND o.seller_id = t.seller_id 
    AND o.goods_id = t.goods_id
SET o.state = '取消'
WHERE o.state = '待支付' AND o.is_bool = 1;

-- 3. 恢复商品库存
UPDATE goods g
INNER JOIN timeout_orders t ON g.goods_id = t.goods_id
SET g.num = g.num + t.num
WHERE g.is_bool = 1;

-- 4. 查看处理结果
SELECT 
    '超时订单处理完成' as message,
    COUNT(*) as processed_orders,
    SUM(t.num) as restored_quantity
FROM timeout_orders t;

-- 5. 查看被取消的订单详情
SELECT 
    o.order_id,
    o.buyer_id,
    o.seller_id,
    o.goods_id,
    o.num,
    o.time,
    o.state
FROM `order` o
INNER JOIN timeout_orders t ON o.buyer_id = t.buyer_id 
    AND o.seller_id = t.seller_id 
    AND o.goods_id = t.goods_id
WHERE o.state = '取消';

-- 6. 查看恢复库存的商品
SELECT 
    g.goods_id,
    g.goods_name,
    t.num as restored_quantity,
    g.num as current_stock
FROM goods g
INNER JOIN timeout_orders t ON g.goods_id = t.goods_id
WHERE g.is_bool = 1;

-- 7. 清理临时表
DROP TEMPORARY TABLE IF EXISTS timeout_orders; 