package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.Order;
import com.retail.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单Repository层
 * 封装OrderMapper的数据访问操作，提供业务语义化的数据查询方法
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final OrderMapper orderMapper;

    /**
     * 创建订单（使用XML中的自定义SQL）
     */
    public int insert(Order order) {
        return orderMapper.createOrder(order);
    }

    /**
     * 根据ID查询订单（使用XML中的自定义SQL）
     */
    public Order findById(Long id) {
        return orderMapper.getOrderById(id);
    }

    /**
     * 根据用户ID查询订单列表
     */
    public List<Order> findByUserId(Long userId) {
        return orderMapper.getOrdersByUserId(userId);
    }

    /**
     * 更新订单状态
     */
    public int updateStatus(Long id, Integer status) {
        return orderMapper.updateOrderStatus(id, status);
    }

    /**
     * 删除订单（逻辑删除）
     */
    public int deleteById(Long id) {
        return orderMapper.deleteOrder(id);
    }

    /**
     * 查询所有订单
     */
    public List<Order> findAll() {
        return orderMapper.getAllOrders();
    }
}
