package com.example.database_cli.server.Impl;

import com.example.database_cli.enums.ResultEnum;
import com.example.database_cli.mapper.GoodsMapper;
import com.example.database_cli.model.entity.Goods;
import com.example.database_cli.model.result.Result;
import com.example.database_cli.model.vo.VoGoods;
import com.example.database_cli.server.IGoodsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    // 图片保存路径 (始终相对于项目根目录)
    private static final String IMAGE_UPLOAD_PATH;
    static {
        // 获取当前项目根目录
        String projectRoot = System.getProperty("user.dir");
        // 拼接到根目录下的 uploaded_images
        IMAGE_UPLOAD_PATH = java.nio.file.Paths.get(projectRoot, "uploaded_images").toString();
    }

    @Override
    public Result addGoodsWithImages(VoGoods voGoods) {
        try {
            // 参数校验
            if (voGoods == null || !StringUtils.hasText(voGoods.getGoodsName()) 
                || !StringUtils.hasText(voGoods.getSellerId())) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }

            if (voGoods.getPrice() <= 0) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }

            if (voGoods.getNum() < 0) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }

            // 创建Goods对象
            Goods goods = new Goods();
            goods.setGoodsName(voGoods.getGoodsName());
            goods.setSellerId(voGoods.getSellerId());
            goods.setType(voGoods.getType());
            goods.setPrice(voGoods.getPrice());
            goods.setNum(voGoods.getNum());

            // 处理图片上传
            List<String> imagePaths = saveImages(voGoods.getImages());
            if (imagePaths == null) {
                return Result.fail(ResultEnum.ERROR_OPERATION);
            }

            // 设置图片路径到Goods对象
            goods.setImages(imagePaths);

            // 保存商品信息
            return addGoods(goods);
        } catch (Exception e) {
            log.error("添加商品时发生异常: ", e);
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    /**
     * 保存图片文件
     * @param imageFiles 图片文件列表
     * @return 图片路径列表，失败返回null
     */
    private List<String> saveImages(List<MultipartFile> imageFiles) {
        List<String> imagePaths = new ArrayList<>();
        
        if (imageFiles == null || imageFiles.isEmpty()) {
            return imagePaths;
        }
        java.io.File uploadDir = new java.io.File(IMAGE_UPLOAD_PATH);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        for (MultipartFile imageFile : imageFiles) {
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String originalFilename = imageFile.getOriginalFilename();
                    String fileExtension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String fileName = "goods_" + java.util.UUID.randomUUID().toString().replace("-", "") + fileExtension;
                    
                    // 保存图片文件到绝对路径
                    java.io.File destFile = new java.io.File(uploadDir, fileName);
                    imageFile.transferTo(destFile);
                    // 保存相对路径到数据库
                    imagePaths.add("http://47.121.122.230:8686/images/"+fileName);
                    log.info("图片保存成功，路径: {}, 文件名: {}", destFile.getAbsolutePath(), fileName);
                } catch (IOException e) {
                    log.error("图片保存失败: {}", imageFile.getOriginalFilename(), e);
                    return null;
                }
            }
        }
        
        return imagePaths;
    }

    @Override
    public Result addGoods(Goods goods) {
        // 参数校验
        if (goods == null || !StringUtils.hasText(goods.getGoodsName()) 
            || !StringUtils.hasText(goods.getSellerId())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        if (goods.getPrice() <= 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        if (goods.getNum() < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 生成商品ID
        goods.setGoodsId("G" + UUID.randomUUID().toString().replace("-", ""));
        
        // 保存商品信息
        int result = goodsMapper.insert(goods);
        if (result > 0) {
            log.info("商品创建成功: 商品ID={}, 商品名称={}, 图片数量={}", 
                    goods.getGoodsId(), goods.getGoodsName(), 
                    goods.getImages() != null ? goods.getImages().size() : 0);
            return Result.success(goods);
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getGoodsById(String goodsId) {
        // 参数校验
        if (!StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询商品信息
        Goods goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        return Result.success(goods);
    }

    @Override
    public Result getAllGoods() {
        // 查询所有商品
        List<Goods> goodsList = goodsMapper.selectAll();
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsBySellerId(String sellerId) {
        // 参数校验
        if (!StringUtils.hasText(sellerId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 查询卖家商品
        List<Goods> goodsList = goodsMapper.selectBySellerId(sellerId);
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsByType(String type) {
        // 参数校验
        if (!StringUtils.hasText(type)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 根据类型查询商品
        List<Goods> goodsList = goodsMapper.selectByType(type);
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsByNameLike(String goodsName) {
        // 参数校验
        if (!StringUtils.hasText(goodsName)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 根据名称模糊查询商品
        List<Goods> goodsList = goodsMapper.selectByGoodsNameLike(goodsName);
        return Result.success(goodsList);
    }

    @Override
    public Result getGoodsByPriceRange(double minPrice, double maxPrice) {
        // 参数校验
        if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 根据价格范围查询商品
        List<Goods> goodsList = goodsMapper.selectByPriceRange(minPrice, maxPrice);
        return Result.success(goodsList);
    }

    @Override
    public Result updateGoods(Goods goods) {
        // 参数校验
        if (goods == null || !StringUtils.hasText(goods.getGoodsId())) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查商品是否存在（加悲观锁）
        Goods existGoods = goodsMapper.selectByIdForUpdate(goods.getGoodsId());
        if (existGoods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 价格校验
        if (goods.getPrice() != 0 && goods.getPrice() <= 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 库存校验
        if (goods.getNum() < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 更新商品信息
        int result = goodsMapper.update(goods);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result updateGoodsStock(String goodsId, int num) {
        // 参数校验
        if (!StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        if (num < 0) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 检查商品是否存在（加悲观锁）
        Goods existGoods = goodsMapper.selectByIdForUpdate(goodsId);
        if (existGoods == null) {
            return Result.fail(ResultEnum.ERROR_NOTFOUND);
        }

        // 更新商品库存
        int result = goodsMapper.updateNum(goodsId, num);
        if (result > 0) {
            return Result.success();
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result deleteGoods(String goodsId) {
        // 参数校验
        if (!StringUtils.hasText(goodsId)) {
            return Result.fail(ResultEnum.ERROR_BADPARMETERS);
        }

        // 删除商品（软删除）
        int result = goodsMapper.deleteById(goodsId);
        if (result > 0) {
            log.info("商品删除成功: 商品ID={}", goodsId);
            return Result.success("商品删除成功");
        } else {
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    // 分页查询相关方法实现
    @Override
    public Result getAllGoodsPaged(int page, int pageSize) {
        try {
            int offset = (page - 1) * pageSize;
            List<Goods> goodsList = goodsMapper.selectAllPaged(offset, pageSize);
            int total = goodsMapper.countAll();
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", goodsList);
            result.put("total", total);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询所有商品时发生异常: ", e);
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getGoodsBySellerIdPaged(String sellerId, int page, int pageSize) {
        try {
            if (!StringUtils.hasText(sellerId)) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }
            
            int offset = (page - 1) * pageSize;
            List<Goods> goodsList = goodsMapper.selectBySellerIdPaged(sellerId, offset, pageSize);
            int total = goodsMapper.countBySellerId(sellerId);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", goodsList);
            result.put("total", total);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询卖家商品时发生异常: ", e);
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getGoodsByTypePaged(String type, int page, int pageSize) {
        try {
            if (!StringUtils.hasText(type)) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }
            
            int offset = (page - 1) * pageSize;
            List<Goods> goodsList = goodsMapper.selectByTypePaged(type, offset, pageSize);
            int total = goodsMapper.countByType(type);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", goodsList);
            result.put("total", total);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询商品类型时发生异常: ", e);
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getGoodsByNameLikePaged(String goodsName, int page, int pageSize) {
        try {
            if (!StringUtils.hasText(goodsName)) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }
            
            int offset = (page - 1) * pageSize;
            List<Goods> goodsList = goodsMapper.selectByGoodsNameLikePaged(goodsName, offset, pageSize);
            int total = goodsMapper.countByGoodsNameLike(goodsName);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", goodsList);
            result.put("total", total);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询商品名称时发生异常: ", e);
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }

    @Override
    public Result getGoodsByPriceRangePaged(double minPrice, double maxPrice, int page, int pageSize) {
        try {
            if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
                return Result.fail(ResultEnum.ERROR_BADPARMETERS);
            }
            
            int offset = (page - 1) * pageSize;
            List<Goods> goodsList = goodsMapper.selectByPriceRangePaged(minPrice, maxPrice, offset, pageSize);
            int total = goodsMapper.countByPriceRange(minPrice, maxPrice);
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("data", goodsList);
            result.put("total", total);
            return Result.success(result);
        } catch (Exception e) {
            log.error("分页查询价格范围时发生异常: ", e);
            return Result.fail(ResultEnum.ERROR_OPERATION);
        }
    }
} 