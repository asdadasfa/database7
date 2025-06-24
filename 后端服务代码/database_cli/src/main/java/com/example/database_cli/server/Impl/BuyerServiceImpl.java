package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.BuyerMapper;
import com.example.database_cli.model.entity.Buyer;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoBuyer;
import com.example.database_cli.model.vo.VoResBuyer;
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
    public Result register(VoBuyer vobuyer) {
        // 参数校验
        if (vobuyer == null || !StringUtils.hasText(vobuyer.getBuyerName())
            || !StringUtils.hasText(vobuyer.getBuyerPassword())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查用户名是否已存在
        Buyer existBuyer = buyerMapper.selectByBuyerName(vobuyer.getBuyerName());
        if (existBuyer != null) {
            return Result.fail(ResultEnum.ERROR_NAMEDUPLICATION);
        }

        // 生成买家ID
        Buyer buyer = new Buyer();
        buyer.setBuyerName(vobuyer.getBuyerName());
        buyer.setBuyerPassword(vobuyer.getBuyerPassword());
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
    public Result login(VoBuyer vobuyer) {
        // 参数校验
        if (!StringUtils.hasText(vobuyer.getBuyerName()) || !StringUtils.hasText(vobuyer.getBuyerPassword())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询买家信息
        Buyer buyer = buyerMapper.selectByBuyerName(vobuyer.getBuyerName());
        if (buyer == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 验证密码
        if (!vobuyer.getBuyerPassword().equals(buyer.getBuyerPassword())) {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }

        // 登录成功，返回买家信息
        VoResBuyer voResBuyer = new VoResBuyer();
        voResBuyer.setBuyerId(buyer.getBuyerId());
        voResBuyer.setBuyerName(buyer.getBuyerName());
        return Result.success(voResBuyer);
    }

    @Override
    public Result logout(String buyerId, String password) {
        // 参数校验
        if (!StringUtils.hasText(buyerId) || !StringUtils.hasText(password)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查买家是否存在
        Buyer buyer = buyerMapper.selectById(buyerId);
        if (buyer == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 验证密码
        if (!password.equals(buyer.getBuyerPassword())) {
            return Result.fail("密码错误");
        }
        // 执行注销操作
        int result = buyerMapper.deleteById(buyerId);
        if (result > 0) {
            return Result.success("注销成功");
        }
        else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
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