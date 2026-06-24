package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.Product;
import com.retail.mapper.AdminProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 管理员商品Repository层
 * 封装AdminProductMapper的数据访问操作，提供业务语义化的数据查询方法
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminProductRepository {

    private final AdminProductMapper adminProductMapper;

    /**
     * 根据ID查询商品
     */
    public Product findById(Long id) {
        return adminProductMapper.selectById(id);
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
    public Page<Product> selectPage(Page<Product> page, LambdaQueryWrapper<Product> wrapper) {
        return adminProductMapper.selectPage(page, wrapper);
    }

    /**
     * 条件查询商品列表
     */
    public List<Product> selectList(LambdaQueryWrapper<Product> wrapper) {
        return adminProductMapper.selectList(wrapper);
    }

    /**
     * 保存商品
     */
    public int insert(Product product) {
        return adminProductMapper.insert(product);
    }

    /**
     * 更新商品
     */
    public int update(Product product) {
        return adminProductMapper.updateById(product);
    }

    /**
     * 更新商品（别名，兼容旧代码）
     */
    public int updateById(Product product) {
        return adminProductMapper.updateById(product);
    }

    /**
     * 根据ID删除商品
     */
    public int deleteById(Long id) {
        return adminProductMapper.deleteById(id);
    }

    /**
     * 批量删除商品
     */
    public int deleteBatchIds(List<Long> ids) {
        return adminProductMapper.deleteBatchIds(ids);
    }

    /**
     * 条件更新商品（兼容MyBatis Plus原生调用方式）
     */
    public int update(Product entity, LambdaUpdateWrapper<Product> updateWrapper) {
        return adminProductMapper.update(entity, updateWrapper);
    }

    /**
     * 条件统计商品数量
     */
    public Long selectCount(LambdaQueryWrapper<Product> wrapper) {
        return adminProductMapper.selectCount(wrapper);
    }

    /**
     * 获取所有不同的产品分类
     */
    public List<Integer> selectDistinctCategories() {
        return adminProductMapper.selectDistinctCategories();
    }

    /**
     * 查询所有商品
     */
    public List<Product> findAll() {
        return adminProductMapper.selectList(null);
    }
}
