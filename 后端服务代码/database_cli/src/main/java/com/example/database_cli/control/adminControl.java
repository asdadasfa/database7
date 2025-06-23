package com.example.database_cli.control;

import com.example.database_cli.model.result.Result;
import com.example.database_cli.server.IAdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class adminControl {
    @Autowired
    private IAdminService adminService;

    @PostMapping("/login")
    public Result login(@RequestParam String id, @RequestParam String password) {
        return adminService.login(id, password);
    }

    @PostMapping("/deleteBuyer")
    public Result deleteBuyer(@RequestParam String buyerId) {
        return adminService.deleteBuyer(buyerId);
    }

    @PostMapping("/restoreBuyer")
    public Result restoreBuyer(@RequestParam String buyerId) {
        return adminService.restoreBuyer(buyerId);
    }

    @PostMapping("/deleteSeller")
    public Result deleteSeller(@RequestParam String sellerId) {
        return adminService.deleteSeller(sellerId);
    }

    @PostMapping("/restoreSeller")
    public Result restoreSeller(@RequestParam String sellerId) {
        return adminService.restoreSeller(sellerId);
    }

    @GetMapping("/buyers")
    public Result getAllBuyers() {
        return adminService.getAllBuyers();
    }

    @GetMapping("/sellers")
    public Result getAllSellers() {
        return adminService.getAllSellers();
    }

    @PostMapping("/deleteGoods")
    public Result deleteGoods(@RequestParam String goodsId) {
        return adminService.deleteGoods(goodsId);
    }
    // ... 其他接口
}
