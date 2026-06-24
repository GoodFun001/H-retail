package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.Order;
import com.retail.mapper.AdminOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 管理员订单Repository层
 * 封装AdminOrderMapper的数据访问操作，提供业务语义化的数据查询方法
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminOrderRepository {

    private final AdminOrderMapper adminOrderMapper;

    /**
     * 根据ID查询订单
     */
    public Order findById(Long id) {
        return adminOrderMapper.selectById(id);
    }

    /**
     * 根据ID查询订单（别名，兼容旧代码）
     */
    public Order selectById(Long id) {
        return findById(id);
    }

    /**
     * 保存订单
     */
    public int insert(Order order) {
        return adminOrderMapper.insert(order);
    }

    /**
     * 更新订单
     */
    public int update(Order order) {
        return adminOrderMapper.updateById(order);
    }

    /**
     * 更新订单（别名，兼容旧代码）
     */
    public int updateById(Order order) {
        return adminOrderMapper.updateById(order);
    }

    /**
     * 根据ID删除订单
     */
    public int deleteById(Long id) {
        return adminOrderMapper.deleteById(id);
    }

    /**
     * 分页查询订单
     */
    public IPage<Order> selectPage(Page<Order> page, LambdaQueryWrapper<Order> wrapper) {
        return adminOrderMapper.selectPage(page, wrapper);
    }

    /**
     * 条件查询订单列表
     */
    public List<Order> selectList(LambdaQueryWrapper<Order> wrapper) {
        return adminOrderMapper.selectList(wrapper);
    }

    /**
     * 条件统计订单数量
     */
    public Long selectCount(LambdaQueryWrapper<Order> wrapper) {
        return adminOrderMapper.selectCount(wrapper);
    }

    /**
     * 查询指定字段的值列表
     */
    public List<Object> selectObjs(LambdaQueryWrapper<Order> wrapper) {
        return adminOrderMapper.selectObjs(wrapper);
    }

    /**
     * 查询所有订单
     */
    public List<Order> findAll() {
        return adminOrderMapper.selectList(null);
    }
}
