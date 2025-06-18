package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.BuyerMapper;
import com.example.database_cli.model.entity.Buyer;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.IBuyerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class BuyerServiceImpl implements IBuyerService {

    @Autowired
    private BuyerMapper buyerMapper;

    @Override
    public Result register(Buyer buyer) {
        // 参数校验
        if (buyer == null || !StringUtils.hasText(buyer.getBuyerName()) 
            || !StringUtils.hasText(buyer.getBuyerPassword())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查用户名是否已存在
        Buyer existBuyer = buyerMapper.selectByBuyerName(buyer.getBuyerName());
        if (existBuyer != null) {
            return Result.fail(ResultEnum.ERROR_NAMEDUPLICATION);
        }

        // 生成买家ID
        buyer.setBuyerId(UUID.randomUUID().toString().replace("-", ""));
        
        // 保存买家信息
        int result = buyerMapper.insert(buyer);
        if (result > 0) {
            return Result.success(buyer);
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result login(String buyerName, String buyerPassword) {
        // 参数校验
        if (!StringUtils.hasText(buyerName) || !StringUtils.hasText(buyerPassword)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询买家信息
        Buyer buyer = buyerMapper.selectByBuyerName(buyerName);
        if (buyer == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 验证密码
        if (!buyerPassword.equals(buyer.getBuyerPassword())) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }

        // 登录成功，返回买家信息
        buyer.setBuyerPassword(null);
        return Result.success(buyer);
    }

    @Override
    public Result logout(String buyerId) {
        // 参数校验
        if (!StringUtils.hasText(buyerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查买家是否存在
        Buyer buyer = buyerMapper.selectById(buyerId);
        if (buyer == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 注销成功（这里可以根据需要添加其他逻辑，比如清除session等）
        return Result.success();
    }

    @Override
    public Result getBuyerById(String buyerId) {
        // 参数校验
        if (!StringUtils.hasText(buyerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询买家信息
        Buyer buyer = buyerMapper.selectById(buyerId);
        if (buyer == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 返回买家信息（不包含密码）
        buyer.setBuyerPassword(null);
        return Result.success(buyer);
    }

    @Override
    public Result updateBuyer(Buyer buyer) {
        // 参数校验
        if (buyer == null || !StringUtils.hasText(buyer.getBuyerId())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查买家是否存在
        Buyer existBuyer = buyerMapper.selectById(buyer.getBuyerId());
        if (existBuyer == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 如果修改用户名，检查新用户名是否已存在
        if (StringUtils.hasText(buyer.getBuyerName()) 
            && !buyer.getBuyerName().equals(existBuyer.getBuyerName())) {
            Buyer nameExistBuyer = buyerMapper.selectByBuyerName(buyer.getBuyerName());
            if (nameExistBuyer != null) {
                return Result.fail(ResultEnum.ERROR_NAMEDUPLICATION);
            }
        }

        // 更新买家信息
        int result = buyerMapper.update(buyer);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }
} 