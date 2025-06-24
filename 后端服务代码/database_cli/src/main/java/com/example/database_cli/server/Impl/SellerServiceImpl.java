package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.SellerMapper;
import com.example.database_cli.model.entity.Seller;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoResSeller;
import com.example.database_cli.model.vo.VoSeller;
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
    public Result register(VoSeller voseller) {
        // 参数校验
        if (voseller == null || !StringUtils.hasText(voseller.getSellerName())
            || !StringUtils.hasText(voseller.getSellerPassword())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查用户名是否已存在
        Seller existSeller = sellerMapper.selectBySellerName(voseller.getSellerName());
        if (existSeller != null) {
            return Result.fail(ResultEnum.ERROR_NAMEDUPLICATION);
        }

        // 生成卖家ID
        Seller seller = new Seller();
        seller.setSellerId(UUID.randomUUID().toString().replace("-", ""));
        seller.setSellerName(voseller.getSellerName());
        seller.setSellerPassword(voseller.getSellerPassword());
        // 保存卖家信息
        int result = sellerMapper.insert(seller);
        if (result > 0) {
            return Result.success(seller);
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result login(VoSeller voseller) {
        // 参数校验
        if (!StringUtils.hasText(voseller.getSellerName()) || !StringUtils.hasText(voseller.getSellerPassword())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询卖家信息
        Seller seller = sellerMapper.selectBySellerName(voseller.getSellerName());
        if (seller == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 验证密码
        if (!voseller.getSellerPassword().equals(seller.getSellerPassword())) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }

        // 登录成功，返回卖家信息
        VoResSeller resSeller = new VoResSeller();
        resSeller.setSellerId(seller.getSellerId());
        resSeller.setSellerName(seller.getSellerName());
        return Result.success(resSeller);
    }

    @Override
    public Result logout(String sellerId, String password) {
        // 参数校验
        if (!StringUtils.hasText(sellerId) || !StringUtils.hasText(password)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查卖家是否存在
        Seller seller = sellerMapper.selectById(sellerId);
        if (seller == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 验证密码
        if (!password.equals(seller.getSellerPassword())) {
            return Result.fail("密码错误");
        }
        int result = sellerMapper.deleteById(sellerId);
        if (result > 0) {
            return Result.success("注销成功");
        }
        else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
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