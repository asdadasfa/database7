package com.example.database_cli.server;

import com.example.database_cli.model.entity.Seller;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoSeller;

public interface ISellerService {
    
    /**
     * 卖家注册
     * @param voseller 卖家信息
     * @return 注册结果
     */
    Result register(VoSeller voseller);
    
    /**
     * 卖家登录
     * @param voseller 卖家登录信息
     * @return 登录结果
     */
    Result login(VoSeller voseller  );
    
    /**
     * 卖家注销
     * @param sellerId 卖家ID
     * @return 注销结果
     */
    Result logout(String sellerId);
    
    /**
     * 根据ID查询卖家信息
     * @param sellerId 卖家ID
     * @return 卖家信息
     */
    Result getSellerById(String sellerId);
    
    /**
     * 更新卖家信息
     * @param seller 卖家信息
     * @return 更新结果
     */
    Result updateSeller(Seller seller);
}
