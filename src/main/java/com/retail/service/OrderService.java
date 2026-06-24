package com.retail.service;

import com.retail.entity.Order;
import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    Order createOrder(Order order);
    
    /**
     * 根据ID查询订单
     */
    Order getOrderById(Long id);
    
    /**
     * 根据用户ID查询订单列表
     */
    List<Order> getOrdersByUserId(Long userId);
    
    /**
     * 更新订单状态
     */
    boolean updateOrderStatus(Long id, Integer status);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long id);
    
    /**
     * 获取所有订单
     */
    List<Order> getAllOrders();
}
