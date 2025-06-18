package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.SellerMapper;
import com.example.database_cli.model.entity.Seller;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.ISellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class SellerServiceImpl implements ISellerService {

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public Result register(Seller seller) {
        // 参数校验
        if (seller == null || !StringUtils.hasText(seller.getSellerName()) 
            || !StringUtils.hasText(seller.getSellerPassword())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查用户名是否已存在
        Seller existSeller = sellerMapper.selectBySellerName(seller.getSellerName());
        if (existSeller != null) {
            return Result.fail(ResultEnum.ERROR_NAMEDUPLICATION);
        }

        // 生成卖家ID
        seller.setSellerId(UUID.randomUUID().toString().replace("-", ""));
        
        // 保存卖家信息
        int result = sellerMapper.insert(seller);
        if (result > 0) {
            return Result.success(seller);
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result login(String sellerName, String sellerPassword) {
        // 参数校验
        if (!StringUtils.hasText(sellerName) || !StringUtils.hasText(sellerPassword)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询卖家信息
        Seller seller = sellerMapper.selectBySellerName(sellerName);
        if (seller == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 验证密码
        if (!sellerPassword.equals(seller.getSellerPassword())) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }

        // 登录成功，返回卖家信息
        seller.setSellerPassword(null);
        return Result.success(seller);
    }

    @Override
    public Result logout(String sellerId) {
        // 参数校验
        if (!StringUtils.hasText(sellerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查卖家是否存在
        Seller seller = sellerMapper.selectById(sellerId);
        if (seller == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 注销成功（这里可以根据需要添加其他逻辑，比如清除session等）
        return Result.success();
    }

    @Override
    public Result getSellerById(String sellerId) {
        // 参数校验
        if (!StringUtils.hasText(sellerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询卖家信息
        Seller seller = sellerMapper.selectById(sellerId);
        if (seller == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 返回卖家信息（不包含密码）
        seller.setSellerPassword(null);
        return Result.success(seller);
    }

    @Override
    public Result updateSeller(Seller seller) {
        // 参数校验
        if (seller == null || !StringUtils.hasText(seller.getSellerId())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查卖家是否存在
        Seller existSeller = sellerMapper.selectById(seller.getSellerId());
        if (existSeller == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 如果修改用户名，检查新用户名是否已存在
        if (StringUtils.hasText(seller.getSellerName()) 
            && !seller.getSellerName().equals(existSeller.getSellerName())) {
            Seller nameExistSeller = sellerMapper.selectBySellerName(seller.getSellerName());
            if (nameExistSeller != null) {
                return Result.fail(ResultEnum.ERROR_NAMEDUPLICATION);
            }
        }

        // 更新卖家信息
        int result = sellerMapper.update(seller);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }
} 