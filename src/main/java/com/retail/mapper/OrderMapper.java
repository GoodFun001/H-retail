package com.retail.mapper;

import com.retail.entity.Order;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单数据访问层
 */
@Mapper
public interface OrderMapper {
    
    /**
     * 创建订单
     */
    int createOrder(Order order);
    
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
    int updateOrderStatus(Long id, Integer status);
    
    /**
     * 删除订单
     */
    int deleteOrder(Long id);
    
    /**
     * 查询所有订单
     */
    List<Order> getAllOrders();
}
