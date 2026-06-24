package com.retail.service;

import com.retail.dto.OrderCreateDTO;
import com.retail.dto.OrderSearchDTO;
import com.retail.dto.OrderUpdateDTO;
import com.retail.entity.Order;
import com.retail.vo.AdminOrderDetailVO;
import com.retail.vo.OrderDetailVO;
import com.retail.vo.OrderVO;

import java.util.List;
import java.util.Map;

/**
 * 管理员订单服务接口
 */
public interface AdminOrderService {

    /**
     * 创建订单
     */
    OrderVO createOrder(OrderCreateDTO createDTO);

    /**
     * 根据ID获取订单详情
     */
    OrderDetailVO getOrderById(Long id);

    /**
     * 获取管理员订单详情（返回AdminOrderDetailVO）
     */
    AdminOrderDetailVO getAdminOrderDetail(Long id);

    /**
     * 搜索订单
     */
    Map<String, Object> searchOrders(OrderSearchDTO searchDTO);

    /**
     * 搜索订单（简化参数版本）
     */
    List<Order> searchOrders(String orderNo, Long userId);

    /**
     * 更新订单
     */
    OrderVO updateOrder(Long id, OrderUpdateDTO updateDTO);

    /**
     * 管理员更新订单（Map参数版本，用于前端JSON直接传递）
     */
    Order adminUpdateOrder(Long id, Map<String, Object> updateData);

    /**
     * 取消订单
     */
    void cancelOrder(Long id);

    /**
     * 确认支付
     */
    void confirmPayment(Long id);

    /**
     * 发货
     */
    void shipOrder(Long id);

    /**
     * 完成订单
     */
    void completeOrder(Long id);

    /**
     * 退款
     */
    void refundOrder(Long id);

    /**
     * 删除订单
     */
    void deleteOrder(Long id);

    /**
     * 获取所有订单
     */
    List<Order> getAllOrders();

    /**
     * 获取订单统计
     */
    Map<String, Object> getOrderStatistics();
}
