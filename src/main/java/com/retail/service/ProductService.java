package com.retail.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.retail.entity.Product;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService extends IService<Product> {

    /**
     * 分页查询商品列表
     */
    IPage<Product> getProductList(Integer pageNum, Integer pageSize, String searchKey, Integer category);

    /**
     * 获取商品详情
     */
    Product getProductDetail(Long productId);

    /**
     * 创建商品（管理员）
     */
    boolean createProduct(Product product, Long adminId);

    /**
     * 更新商品（管理员）
     */
    boolean updateProduct(Product product, Long adminId);

    /**
     * 删除商品（管理员）
     */
    boolean deleteProduct(Long productId, Long adminId);

    /**
     * 获取热点商品列表
     */
    List<Product> getHotProducts();

    /**
     * 刷新热点商品缓存
     */
    void refreshHotProductsCache();

    /**
     * 计算VIP折扣价格
     */
    Product calculateVipPrice(Product product, boolean isVip);
}
