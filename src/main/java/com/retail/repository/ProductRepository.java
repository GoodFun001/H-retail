package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.Product;
import com.retail.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商品Repository层
 * 封装ProductMapper的数据访问操作，提供业务语义化的数据查询方法
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRepository {

    private final ProductMapper productMapper;

    /**
     * 根据ID查询商品
     */
    public Product findById(Long id) {
        return productMapper.selectById(id);
    }

    /**
     * 根据ID查询商品（别名，兼容旧代码）
     */
    public Product selectById(Long id) {
        return findById(id);
    }

    /**
     * 分页查询商品
     */
    public IPage<Product> selectPage(Page<Product> page, LambdaQueryWrapper<Product> wrapper) {
        return productMapper.selectPage(page, wrapper);
    }

    /**
     * 条件查询商品列表
     */
    public List<Product> selectList(LambdaQueryWrapper<Product> wrapper) {
        return productMapper.selectList(wrapper);
    }

    /**
     * 直接查询商品列表（绕过逻辑删除过滤器）
     */
    public List<Product> selectListDirect(LambdaQueryWrapper<Product> wrapper) {
        return productMapper.selectList(wrapper);
    }

    /**
     * 保存商品
     */
    public boolean save(Product product) {
        return productMapper.insert(product) > 0;
    }

    /**
     * 保存商品（别名，兼容旧代码）
     */
    public int insert(Product product) {
        return productMapper.insert(product);
    }

    /**
     * 更新商品
     */
    public boolean update(Product product) {
        return productMapper.updateById(product) > 0;
    }

    /**
     * 更新商品（别名，兼容旧代码）
     */
    public int updateById(Product product) {
        return productMapper.updateById(product);
    }

    /**
     * 根据ID删除商品（逻辑删除）
     */
    public boolean deleteById(Long id) {
        return productMapper.deleteById(id) > 0;
    }

    /**
     * 查询所有商品
     */
    public List<Product> findAll() {
        return productMapper.selectList(null);
    }

    /**
     * 统计商品数量
     */
    public Long count() {
        return productMapper.selectCount(null);
    }

    /**
     * 条件统计商品数量
     */
    public Long count(LambdaQueryWrapper<Product> wrapper) {
        return productMapper.selectCount(wrapper);
    }

    /**
     * 条件更新商品
     */
    public boolean update(LambdaQueryWrapper<Product> wrapper) {
        return productMapper.update(null, wrapper) > 0;
    }

    /**
     * 批量更新（使用MyBatis Plus Service层能力）
     */
    public boolean updateBatchById(List<Product> products) {
        for (Product product : products) {
            if (productMapper.updateById(product) <= 0) return false;
        }
        return true;
    }
}
