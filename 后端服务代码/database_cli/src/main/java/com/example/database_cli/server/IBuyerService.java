package com.example.database_cli.server;

import com.example.database_cli.model.entity.Buyer;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoBuyer;

public interface IBuyerService {
    
    /**
     * 买家注册
     * @param vobuyer 买家信息
     * @return 注册结果
     */
    Result register(VoBuyer vobuyer);
    
    /**
     * 买家登录
     * @param vobuyer 买家登录信息
     * @return 登录结果
     */
    Result login(VoBuyer vobuyer);
    
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