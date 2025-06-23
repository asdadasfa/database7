package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.CartMapper;
import com.example.database_cli.mapper.GoodsMapper;
import com.example.database_cli.model.entity.Cart;
import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoCart;
import com.example.database_cli.model.vo.VoCartList;
import com.example.database_cli.server.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Result addToCart(String buyerId, String goodsId, int num) {
        // 参数校验
        if (!StringUtils.hasText(buyerId) || !StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        if (num <= 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        try {
            // 检查商品是否存在
            Goods goods = goodsMapper.selectById(goodsId);
            if (goods == null) {
                return Result.fail(ResultEnum.ERROR_NOTFOUND);
            }
            
            // 检查库存是否充足
            if (goods.getNum() < num) {
                return Result.fail(ResultEnum.ERROR_OPERATION);
            }
            
            // 检查购物车中是否已存在该商品
            Cart existingCart = cartMapper.selectByBuyerIdAndGoodsId(buyerId, goodsId);
            
            if (existingCart != null) {
                // 如果已存在，更新数量
                int newNum = existingCart.getNum() + num;
                // 再次检查库存
                if (goods.getNum() < newNum) {
                    return Result.fail(ResultEnum.ERROR_OPERATION);
                }
                existingCart.setNum(newNum);
                int result = cartMapper.update(existingCart);
                if (result > 0) {
                    return Result.success("商品数量已更新");
                } else {
                    return Result.fail(ResultEnum.ERROR_OPERATION);
                }
            } else {
                // 如果不存在，新增购物车项
                Cart cart = new Cart();
                cart.setBuyerId(buyerId);
                cart.setGoodsId(goodsId);
                cart.setNum(num);
                
                int result = cartMapper.insert(cart);
                if (result > 0) {
                    return Result.success("商品已添加到购物车");
                } else {
                    return Result.fail(ResultEnum.ERROR_OPERATION);
                }
            }
        } catch (Exception e) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getCartContents(String buyerId) {
        // 参数校验
        if (!StringUtils.hasText(buyerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        try {
            // 获取购物车项
            List<Cart> cartItems = cartMapper.selectByBuyerId(buyerId);
            
            if (cartItems.isEmpty()) {
                return Result.success(new VoCartList(new ArrayList<>(), 0.0, 0));
            }
            
            // 构建购物车内容（使用VoCart）
            List<VoCart> voCartItems = new ArrayList<>();
            double totalAmount = 0.0;
            
            for (Cart cartItem : cartItems) {
                Goods goods = goodsMapper.selectById(cartItem.getGoodsId());
                if (goods != null) {
                    VoCart voCart = new VoCart();
                    voCart.setBuyerId(cartItem.getBuyerId());
                    voCart.setGoodsId(cartItem.getGoodsId());
                    voCart.setNum(cartItem.getNum());
                    voCart.setGoodsName(goods.getGoodsName());
                    voCart.setPrice(goods.getPrice());
                    voCart.setType(goods.getType());
                    voCart.setSellerId(goods.getSellerId());
                    voCart.setGoodsImages(goods.getImages());
                    voCart.setSum(goods.getPrice() * cartItem.getNum());
                    
                    totalAmount += goods.getPrice() * cartItem.getNum();
                    voCartItems.add(voCart);
                }
            }
            
            // 构建返回结果
            VoCartList voCartList = new VoCartList(voCartItems, totalAmount, voCartItems.size());
            
            return Result.success(voCartList);
        } catch (Exception e) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result updateCartItem(String buyerId, String goodsId, int num) {
        // 参数校验
        if (!StringUtils.hasText(buyerId) || !StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        if (num < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        try {
            // 检查购物车项是否存在
            Cart existingCart = cartMapper.selectByBuyerIdAndGoodsId(buyerId, goodsId);
            if (existingCart == null) {
                return Result.fail(ResultEnum.ERROR_NOTFOUND);
            }
            
            if (num == 0) {
                // 如果数量为0，删除该购物车项
                return removeFromCart(buyerId, goodsId);
            }
            
            // 检查库存是否充足
            Goods goods = goodsMapper.selectById(goodsId);
            if (goods != null && goods.getNum() < num) {
                return Result.fail(ResultEnum.ERROR_OPERATION);
            }
            
            // 更新数量
            existingCart.setNum(num);
            int result = cartMapper.update(existingCart);
            if (result > 0) {
                return Result.success("购物车商品数量已更新");
            } else {
                return Result.fail(ResultEnum.ERROR_OPERATION);
            }
        } catch (Exception e) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result removeFromCart(String buyerId, String goodsId) {
        // 参数校验
        if (!StringUtils.hasText(buyerId) || !StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        try {
            int result = cartMapper.deleteByBuyerIdAndGoodsId(buyerId, goodsId);
            if (result > 0) {
                return Result.success("商品已从购物车移除");
            } else {
                return Result.fail(ResultEnum.ERROR_NOTFOUND);
            }
        } catch (Exception e) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result clearCart(String buyerId) {
        // 参数校验
        if (!StringUtils.hasText(buyerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        
        try {
            int result = cartMapper.deleteByBuyerId(buyerId);
            if (result > 0) {
                return Result.success("购物车已清空");
            } else {
                return Result.success("购物车已经是空的");
            }
        } catch (Exception e) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getCartContentsPaged(String buyerId, int page, int pageSize) {
        if (!StringUtils.hasText(buyerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }
        try {
            int offset = (page - 1) * pageSize;
            List<Cart> cartItems = cartMapper.selectByBuyerIdPaged(buyerId, offset, pageSize);
            int total = cartMapper.countByBuyerId(buyerId);

            List<VoCart> voCartItems = new ArrayList<>();
            double totalAmount = 0.0;
            for (Cart cartItem : cartItems) {
                Goods goods = goodsMapper.selectById(cartItem.getGoodsId());
                if (goods != null) {
                    VoCart voCart = new VoCart();
                    voCart.setBuyerId(cartItem.getBuyerId());
                    voCart.setGoodsId(cartItem.getGoodsId());
                    voCart.setNum(cartItem.getNum());
                    voCart.setGoodsName(goods.getGoodsName());
                    voCart.setPrice(goods.getPrice());
                    voCart.setType(goods.getType());
                    voCart.setSellerId(goods.getSellerId());
                    voCart.setGoodsImages(goods.getImages());
                    voCart.setSum(goods.getPrice() * cartItem.getNum());
                    totalAmount += goods.getPrice() * cartItem.getNum();
                    voCartItems.add(voCart);
                }
            }
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", voCartItems);
            result.put("total", total);
            result.put("totalAmount", totalAmount);
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }
} 