package com.example.database_cli.server;

import com.example.database_cli.model.entity.Buyer;
import com.example.database_cli.model.result.Result;

public interface IBuyerService {
    
    /**
     * 买家注册
     * @param buyer 买家信息
     * @return 注册结果
     */
    Result register(Buyer buyer);
    
    /**
     * 买家登录
     * @param buyerName 买家名称
     * @param buyerPassword 买家密码
     * @return 登录结果
     */
    Result login(String buyerName, String buyerPassword);
    
    /**
     * 买家注销
     * @param buyerId 买家ID
     * @return 注销结果
     */
    Result logout(String buyerId);
    
    /**
     * 根据ID查询买家信息
     * @param buyerId 买家ID
     * @return 买家信息
     */
    Result getBuyerById(String buyerId);
    
    /**
     * 更新买家信息
     * @param buyer 买家信息
     * @return 更新结果
     */
    Result updateBuyer(Buyer buyer);
} 