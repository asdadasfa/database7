package com.example.database_cli.server.Impl;

import com.example.database_cli.mapper.AdminMapper;
import com.example.database_cli.mapper.BuyerMapper;
import com.example.database_cli.mapper.SellerMapper;
import com.example.database_cli.mapper.GoodsMapper;
import com.example.database_cli.model.entity.Admin;
import com.example.database_cli.model.entity.Buyer;
import com.example.database_cli.model.entity.Seller;
import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements IAdminService {
    @Autowired
    private AdminMapper adminMapper;
    @Autowired
    private BuyerMapper buyerMapper;
    @Autowired
    private SellerMapper sellerMapper;
    @Autowired
    private GoodsMapper goodsMapper;

    @Override
    public Result login(String id, String password) {
        Admin admin = adminMapper.selectById(id);
        if (admin != null && admin.getPassword() != null && admin.getPassword().equals(password) && admin.isBool()) {
            return Result.success(admin);
        }
        return Result.fail("账号或密码错误");
    }

    @Override
    public Result deleteBuyer(String buyerId) {
        Buyer buyer = buyerMapper.selectByIdForAdmin(buyerId);
        if (buyer == null) return Result.fail("买家不存在");
        buyer.setBool(false);
        int res = buyerMapper.update(buyer);
        return res > 0 ? Result.success("删除成功") : Result.fail("删除失败");
    }

    @Override
    public Result restoreBuyer(String buyerId) {
        Buyer buyer = buyerMapper.selectByIdForAdmin(buyerId);
        if (buyer == null) return Result.fail("买家不存在");
        buyer.setBool(true);
        int res = buyerMapper.update(buyer);
        return res > 0 ? Result.success("恢复成功") : Result.fail("恢复失败");
    }

    @Override
    public Result deleteSeller(String sellerId) {
        Seller seller = sellerMapper.selectByIdForAdmin(sellerId);
        if (seller == null) return Result.fail("卖家不存在");
        seller.setBool(false);
        int res = sellerMapper.update(seller);
        return res > 0 ? Result.success("删除成功") : Result.fail("删除失败");
    }

    @Override
    public Result restoreSeller(String sellerId) {
        Seller seller = sellerMapper.selectByIdForAdmin(sellerId);
        if (seller == null) return Result.fail("卖家不存在");
        seller.setBool(true);
        int res = sellerMapper.update(seller);
        return res > 0 ? Result.success("恢复成功") : Result.fail("恢复失败");
    }

    @Override
    public Result getAllBuyers() {
        List<Buyer> buyers = buyerMapper.selectAllForAdmin();
        return Result.success(buyers);
    }

    @Override
    public Result getAllSellers() {
        List<Seller> sellers = sellerMapper.selectAllForAdmin();
        return Result.success(sellers);
    }

    @Override
    public Result deleteGoods(String goodsId) {
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) return Result.fail("商品不存在");
        goods.setBool(false);
        int res = goodsMapper.update(goods);
        return res > 0 ? Result.success("删除成功") : Result.fail("删除失败");
    }

    @Override
    public Result getAllBuyersPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Buyer> buyers = buyerMapper.selectAllPaged(offset, pageSize);
        int total = buyerMapper.countAll();
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("data", buyers);
        result.put("total", total);
        return Result.success(result);
    }

    @Override
    public Result getAllSellersPaged(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Seller> sellers = sellerMapper.selectAllPaged(offset, pageSize);
        int total = sellerMapper.countAll();
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("data", sellers);
        result.put("total", total);
        return Result.success(result);
    }
}
