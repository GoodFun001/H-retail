package com.retail.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.common.Result;
import com.retail.entity.Product;
import com.retail.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员商品管理控制器
 */
@RestController
@RequestMapping("/admin/products")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员商品管理", description = "管理员对商品进行管理的相关接口")
public class AdminProductController {

    private final ProductService productService;

    /**
     * 获取商品列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取商品列表", description = "管理员获取商品列表，支持分页")
    public Result<IPage<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Page<Product> page = new Page<>(pageNum, pageSize);
            IPage<Product> productList = productService.page(page);
            return Result.success(productList);
        } catch (Exception e) {
            log.error("获取商品列表失败: {}", e.getMessage(), e);
            return Result.error("获取商品列表失败");
        }
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    @Operation(summary = "搜索商品", description = "管理员搜索商品")
    public Result<IPage<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            IPage<Product> productList = productService.getProductList(pageNum, pageSize, keyword, null);
            return Result.success(productList);
        } catch (Exception e) {
            log.error("搜索商品失败: {}", e.getMessage(), e);
            return Result.error("搜索商品失败");
        }
    }

    /**
     * 创建商品
     */
    @PostMapping
    @Operation(summary = "创建商品", description = "管理员创建新商品")
    public Result<String> createProduct(@Valid @RequestBody Product product, HttpServletRequest request) {
        try {
            // 暂时使用固定的管理员ID，实际项目中应该从JWT token中获取
            Long adminId = 1L;
            boolean success = productService.createProduct(product, adminId);
            if (success) {
                return Result.success("创建商品成功");
            } else {
                return Result.error("创建商品失败");
            }
        } catch (Exception e) {
            log.error("创建商品失败: {}", e.getMessage(), e);
            return Result.error("创建商品失败");
        }
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "管理员根据商品ID获取商品详细信息")
    public Result<Product> getProductDetail(
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

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新商品", description = "管理员更新商品信息")
    public Result<String> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Valid @RequestBody Product product, HttpServletRequest request) {
        try {
            // 暂时使用固定的管理员ID，实际项目中应该从JWT token中获取
            Long adminId = 1L;
            product.setId(id);
            boolean success = productService.updateProduct(product, adminId);
            if (success) {
                return Result.success("更新商品成功");
            } else {
                return Result.error("更新商品失败");
            }
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage(), e);
            return Result.error("更新商品失败");
        }
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "管理员删除商品")
    public Result<String> deleteProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id, HttpServletRequest request) {
        try {
            // 暂时使用固定的管理员ID，实际项目中应该从JWT token中获取
            Long adminId = 1L;
            boolean success = productService.deleteProduct(id, adminId);
            if (success) {
                return Result.success("删除商品成功");
            } else {
                return Result.error("删除商品失败");
            }
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return Result.error("删除商品失败");
        }
    }

    /**
     * 更新商品库存
     */
    @PutMapping("/{id}/stock")
    @Operation(summary = "更新商品库存", description = "管理员更新商品库存")
    public Result<String> updateProductStock(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @RequestParam Integer stock) {
        try {
            Product product = productService.getById(id);
            if (product != null) {
                product.setStock(stock);
                productService.updateById(product);
                return Result.success("更新商品库存成功");
            } else {
                return Result.error("商品不存在");
            }
        } catch (Exception e) {
            log.error("更新商品库存失败: {}", e.getMessage(), e);
            return Result.error("更新商品库存失败");
        }
    }

    /**
     * 更新商品状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新商品状态", description = "管理员更新商品状态（上架/下架）")
    public Result<String> updateProductStatus(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            Product product = productService.getById(id);
            if (product != null) {
                product.setStatus(status);
                productService.updateById(product);
                return Result.success("更新商品状态成功");
            } else {
                return Result.error("商品不存在");
            }
        } catch (Exception e) {
            log.error("更新商品状态失败: {}", e.getMessage(), e);
            return Result.error("更新商品状态失败");
        }
    }

    /**
     * 批量更新商品库存
     */
    @PutMapping("/batch/stock")
    @Operation(summary = "批量更新商品库存", description = "管理员批量更新商品库存")
    public Result<String> batchUpdateProductStock(@RequestBody List<Map<String, Object>> productStockList) {
        try {
            List<Product> products = productStockList.stream().map(map -> {
                Long id = Long.valueOf(map.get("id").toString());
                Integer stock = Integer.valueOf(map.get("stock").toString());
                Product product = new Product();
                product.setId(id);
                product.setStock(stock);
                return product;
            }).collect(Collectors.toList());
            
            productService.updateBatchById(products);
            return Result.success("批量更新商品库存成功");
        } catch (Exception e) {
            log.error("批量更新商品库存失败: {}", e.getMessage(), e);
            return Result.error("批量更新商品库存失败");
        }
    }

    /**
     * 批量更新商品状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新商品状态", description = "管理员批量更新商品状态")
    public Result<String> batchUpdateProductStatus(@RequestBody List<Map<String, Object>> productStatusList) {
        try {
            List<Product> products = productStatusList.stream().map(map -> {
                Long id = Long.valueOf(map.get("id").toString());
                Integer status = Integer.valueOf(map.get("status").toString());
                Product product = new Product();
                product.setId(id);
                product.setStatus(status);
                return product;
            }).collect(Collectors.toList());
            
            productService.updateBatchById(products);
            return Result.success("批量更新商品状态成功");
        } catch (Exception e) {
            log.error("批量更新商品状态失败: {}", e.getMessage(), e);
            return Result.error("批量更新商品状态失败");
        }
    }
}