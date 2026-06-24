package com.retail.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.retail.entity.Product;
import com.retail.mapper.ProductMapper;
import com.retail.repository.ProductRepository;
import com.retail.service.ProductService;
import com.retail.service.UserService;
import com.retail.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 商品服务实现类
 * 【重构说明】引入Repository层，Service通过ProductRepository访问数据，
 * 不再直接依赖ProductMapper，符合分层架构原则。
 */
@Slf4j
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @Value("${retail.hot-product-limit}")
    private Integer hotProductLimit;

    @Value("${retail.hot-product-cache-time}")
    private Integer hotProductCacheTime;

    @Value("${retail.vip-discount-rate}")
    private Double vipDiscountRate;

    private static final String HOT_PRODUCT_CACHE_KEY = "hot_product:list";

    @Override
    public IPage<Product> getProductList(Integer pageNum, Integer pageSize, String searchKey, Integer category) {
        try {
            log.info("开始查询商品列表: pageNum={}, pageSize={}, searchKey={}, category={}", pageNum, pageSize, searchKey, category);
            
            Page<Product> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
            
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            
            // 搜索条件
            if (searchKey != null && !searchKey.trim().isEmpty()) {
                wrapper.and(w -> w.like(Product::getName, searchKey)
                                  .or()
                                  .like(Product::getDescription, searchKey));
                log.info("添加搜索条件: {}", searchKey);
            }
            
            // 分类条件
            if (category != null) {
                wrapper.eq(Product::getCategory, category);
                log.info("添加分类条件: {}", category);
            }
            
            // 只查询上架商品
            wrapper.eq(Product::getStatus, 1);
            log.info("添加状态条件: 只查询上架商品(status=1)");
            
            // 按创建时间降序
            wrapper.orderByDesc(Product::getCreateTime);
            log.info("添加排序条件: 按创建时间降序");
            
            // 先尝试使用非分页查询，查看是否能获取到数据
            List<Product> allProducts = productRepository.selectList(wrapper);
            log.info("非分页查询结果数量: {}", allProducts != null ? allProducts.size() : 0);
            
            IPage<Product> result = productRepository.selectPage(page, wrapper);
            log.info("分页查询结果: total={}, records={}", result.getTotal(), result.getRecords().size());
            
            // 检查是否有记录
            if (result.getRecords().isEmpty()) {
                log.warn("分页查询返回空列表");
                // 尝试不使用分页插件直接查询
                List<Product> directProducts = productRepository.selectListDirect(wrapper);
                log.info("直接查询结果数量: {}", directProducts != null ? directProducts.size() : 0);
            }
            
            return result;
        } catch (Exception e) {
            log.error("查询商品列表失败: {}", e.getMessage(), e);
            // 发生异常时返回空的Page对象而不是null，确保前端能正常处理
            return new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        }
    }

    @Override
    public Product getProductDetail(Long productId) {
        try {
            // 先从缓存获取
            String cacheKey = "product:detail:" + productId;
            Product product = (Product) redisUtil.get(cacheKey);
            
            if (product != null) {
                log.info("从缓存获取商品详情: productId={}", productId);
                return product;
            }
            
            // 缓存未命中，通过Repository查询数据库
            product = productRepository.findById(productId);
            if (product != null && product.getStatus() == 1) {
                // 缓存商品详情，缓存5分钟
                redisUtil.set(cacheKey, product, 5, TimeUnit.MINUTES);
                log.info("从数据库获取商品详情并缓存: productId={}", productId);
            }
            
            return product;
        } catch (Exception e) {
            log.error("获取商品详情失败: productId={}, error={}", productId, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean createProduct(Product product, Long adminId) {
        try {
            product.setCreateBy(adminId);
            product.setStatus(1); // 默认上架
            product.setSalesCount(0); // 初始销量为0
            
            boolean result = productRepository.save(product);
            if (result) {
                log.info("创建商品成功: productId={}, name={}", product.getId(), product.getName());
                // 清除热点商品缓存
                refreshHotProductsCache();
            }
            return result;
        } catch (Exception e) {
            log.error("创建商品失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateProduct(Product product, Long adminId) {
        try {
            Product existingProduct = productRepository.findById(product.getId());
            if (existingProduct == null) {
                log.warn("商品不存在: productId={}", product.getId());
                return false;
            }
            
            // 检查权限
            if (!adminId.equals(existingProduct.getCreateBy())) {
                log.warn("无权限修改商品: productId={}, adminId={}", product.getId(), adminId);
                return false;
            }
            
            boolean result = productRepository.update(product);
            if (result) {
                log.info("更新商品成功: productId={}", product.getId());
                // 清除相关缓存
                clearProductCache(product.getId());
                refreshHotProductsCache();
            }
            return result;
        } catch (Exception e) {
            log.error("更新商品失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteProduct(Long productId, Long adminId) {
        try {
            Product product = productRepository.findById(productId);
            if (product == null) {
                log.warn("商品不存在: productId={}", productId);
                return false;
            }
            
            // 检查权限
            if (!adminId.equals(product.getCreateBy())) {
                log.warn("无权限删除商品: productId={}, adminId={}", productId, adminId);
                return false;
            }
            
            boolean result = productRepository.deleteById(productId);
            if (result) {
                log.info("删除商品成功: productId={}", productId);
                // 清除相关缓存
                clearProductCache(productId);
                refreshHotProductsCache();
            }
            return result;
        } catch (Exception e) {
            log.error("删除商品失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Product> getHotProducts() {
        try {
            // 先从缓存获取
            String cacheData = redisUtil.getString(HOT_PRODUCT_CACHE_KEY);
            if (cacheData != null) {
                log.info("从缓存获取热点商品");
                return JSON.parseArray(cacheData, Product.class);
            }
            
            // 缓存未命中，通过Repository查询数据库
            LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Product::getStatus, 1)
                   .orderByDesc(Product::getSalesCount)
                   .last("LIMIT " + hotProductLimit);
            
            List<Product> hotProducts = productRepository.selectList(wrapper);
            
            // 缓存热点商品
            redisUtil.setString(HOT_PRODUCT_CACHE_KEY, JSON.toJSONString(hotProducts), 
                           hotProductCacheTime, TimeUnit.MINUTES);
            
            log.info("从数据库获取热点商品并缓存: 数量={}", hotProducts.size());
            return hotProducts;
        } catch (Exception e) {
            log.error("获取热点商品失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void refreshHotProductsCache() {
        try {
            // 删除缓存
            redisUtil.delete(HOT_PRODUCT_CACHE_KEY);
            log.info("清除热点商品缓存");
        } catch (Exception e) {
            log.error("刷新热点商品缓存失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public Product calculateVipPrice(Product product, boolean isVip) {
        if (product == null) {
            return null;
        }
        
        if (isVip && vipDiscountRate != null) {
            // 创建副本避免修改原对象
            Product vipProduct = new Product();
            vipProduct.setId(product.getId());
            vipProduct.setName(product.getName());
            vipProduct.setDescription(product.getDescription());
            vipProduct.setPrice(product.getPrice().multiply(BigDecimal.valueOf(vipDiscountRate)));
            vipProduct.setStock(product.getStock());
            vipProduct.setImageUrl(product.getImageUrl());
            vipProduct.setCategory(product.getCategory());
            vipProduct.setStatus(product.getStatus());
            vipProduct.setSalesCount(product.getSalesCount());
            vipProduct.setCreateBy(product.getCreateBy());
            vipProduct.setCreateTime(product.getCreateTime());
            vipProduct.setUpdateTime(product.getUpdateTime());
            
            return vipProduct;
        }
        
        return product;
    }

    /**
     * 清除商品相关缓存
     */
    private void clearProductCache(Long productId) {
        try {
            String detailCacheKey = "product:detail:" + productId;
            redisUtil.delete(detailCacheKey);
            log.info("清除商品缓存: productId={}", productId);
        } catch (Exception e) {
            log.error("清除商品缓存失败: {}", e.getMessage(), e);
        }
    }
}
