package com.retail.controller;

import com.retail.common.Result;
import com.retail.entity.Product;
import com.retail.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 商品控制器
 */
@Slf4j
@RestController
@RequestMapping("/products")
@Tag(name = "商品管理", description = "商品查询相关接口")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping
    @Operation(summary = "获取商品列表", description = "获取所有可用商品列表")
    public Result<IPage<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey,
            @RequestParam(required = false) Integer category) {
        try {
            IPage<Product> products = productService.getProductList(pageNum, pageSize, searchKey, category);
            return Result.success(products);
        } catch (Exception e) {
            log.error("获取商品列表失败: {}", e.getMessage(), e);
            return Result.error("获取商品列表失败");
        }
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "根据关键词搜索商品")
    public Result<IPage<Product>> searchProducts(
            @Parameter(description = "搜索关键词", required = true) @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<Product> products = productService.getProductList(pageNum, pageSize, keyword, null);
            return Result.success(products);
        } catch (Exception e) {
            log.error("搜索商品失败: {}", e.getMessage(), e);
            return Result.error("搜索商品失败");
        }
    }

    /**
     * 按分类获取商品
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "按分类获取商品", description = "根据商品分类获取商品列表")
    public Result<IPage<Product>> getProductsByCategory(
            @Parameter(description = "商品分类", required = true) @PathVariable Integer category,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<Product> products = productService.getProductList(pageNum, pageSize, null, category);
            return Result.success(products);
        } catch (Exception e) {
            log.error("按分类获取商品失败: {}", e.getMessage(), e);
            return Result.error("按分类获取商品失败");
        }
    }

    /**
     * 获取商品分类列表
     */
    @GetMapping("/categories")
    @Operation(summary = "获取商品分类列表", description = "获取所有商品分类")
    public Result<List<CategoryVO>> getProductCategories() {
        try {
            // 获取所有商品，提取唯一的分类值
            IPage<Product> allProducts = productService.getProductList(1, 1000, null, null);
            List<CategoryVO> categories = allProducts.getRecords().stream()
                    .map(Product::getCategory)
                    .distinct()
                    .sorted()
                    .map(category -> {
                        CategoryVO categoryVO = new CategoryVO();
                        categoryVO.setId(category);
                        // 根据分类ID获取对应的分类名称
                        categoryVO.setName(getCategoryName(category));
                        return categoryVO;
                    })
                    .toList();
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取商品分类列表失败: {}", e.getMessage(), e);
            return Result.error("获取商品分类列表失败");
        }
    }
    
    /**
     * 根据分类ID获取分类名称
     */
    private String getCategoryName(Integer categoryId) {
        switch (categoryId) {
            case 1: return "手机数码";
            case 2: return "电脑办公";
            case 3: return "家用电器";
            case 4: return "服装鞋包";
            case 5: return "图书音像";
            default: return "其他";
        }
    }
    
    /**
     * 分类VO类
     */
    private static class CategoryVO {
        private Integer id;
        private String name;
        
        public Integer getId() {
            return id;
        }
        
        public void setId(Integer id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * 根据ID获取商品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据商品ID获取商品详细信息")
    public Result<Product> getProductById(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id) {
        try {
            Product product = productService.getProductDetail(id);
            if (product != null) {
                return Result.success(product);
            } else {
                return Result.error("商品不存在");
            }
        } catch (Exception e) {
            log.error("获取商品详情失败: {}", e.getMessage(), e);
            return Result.error("获取商品详情失败");
        }
    }
}
