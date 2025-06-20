-- 简化版：处理超时订单SQL脚本
-- 将待支付状态超过15分钟的订单转为取消，并将商品数量加回去

-- 1. 更新超时订单状态为取消，同时恢复商品库存
UPDATE goods g
INNER JOIN `order` o ON g.goods_id = o.goods_id
SET 
    g.num = g.num + o.num,
    o.state = '取消'
WHERE o.state = '待支付' 
  AND o.is_bool = 1 
  AND g.is_bool = 1
  AND TIMESTAMPDIFF(MINUTE, STR_TO_DATE(o.time, '%Y-%m-%d %H:%i:%s'), NOW()) > 15;

-- 2. 查看处理结果
SELECT 
    '超时订单处理完成' as message,
    ROW_COUNT() as affected_rows;

-- 3. 查看当前待支付订单数量
SELECT 
    '当前待支付订单' as status,
    COUNT(*) as count
FROM `order` 
WHERE state = '待支付' AND is_bool = 1;

-- 4. 查看已取消订单数量
SELECT 
    '已取消订单' as status,
    COUNT(*) as count
FROM `order` 
WHERE state = '取消' AND is_bool = 1; 