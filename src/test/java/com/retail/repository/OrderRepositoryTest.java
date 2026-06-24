package com.retail.repository;

import com.retail.entity.Order;
import com.retail.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderRepository 测试")
class OrderRepositoryTest {

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD001");
        order.setUserId(100L);
        order.setProductId(200L);
        order.setQuantity(2);
        order.setTotalAmount(new BigDecimal("198.00"));
        order.setStatus(0);
    }

    @Test
    @DisplayName("insert - 创建订单")
    void insert() {
        when(orderMapper.createOrder(order)).thenReturn(1);
        assertEquals(1, orderRepository.insert(order));
    }

    @Test
    @DisplayName("findById - 查询订单")
    void findById() {
        when(orderMapper.getOrderById(1L)).thenReturn(order);
        Order result = orderRepository.findById(1L);
        assertNotNull(result);
        assertEquals("ORD001", result.getOrderNo());
    }

    @Test
    @DisplayName("findByUserId - 按用户查询")
    void findByUserId() {
        when(orderMapper.getOrdersByUserId(100L)).thenReturn(Arrays.asList(order));
        List<Order> list = orderRepository.findByUserId(100L);
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("updateStatus - 更新状态")
    void updateStatus() {
        when(orderMapper.updateOrderStatus(1L, 1)).thenReturn(1);
        assertEquals(1, orderRepository.updateStatus(1L, 1));
    }

    @Test
    @DisplayName("deleteById - 删除订单")
    void deleteById() {
        when(orderMapper.deleteOrder(1L)).thenReturn(1);
        assertEquals(1, orderRepository.deleteById(1L));
    }

    @Test
    @DisplayName("findAll - 查询所有")
    void findAll() {
        when(orderMapper.getAllOrders()).thenReturn(Arrays.asList(order));
        List<Order> list = orderRepository.findAll();
        assertEquals(1, list.size());
    }
}
