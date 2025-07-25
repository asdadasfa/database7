package com.example.database_cli.server;

import com.example.database_cli.model.result.Result;
import java.util.HashMap;
import java.util.Map;

public interface IAdminService {
    Result login(String id, String password);
    Result deleteBuyer(String buyerId);
    Result restoreBuyer(String buyerId);
    Result deleteSeller(String sellerId);
    Result restoreSeller(String sellerId);
    Result getAllBuyers();
    Result getAllSellers();
    Result deleteGoods(String goodsId);
    Result getAllBuyersPaged(int page, int pageSize);
    Result getAllSellersPaged(int page, int pageSize);
} 