package com.retail.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.retail.common.OrderStatusEnum;
import com.retail.entity.Order;
import com.retail.entity.Product;
import com.retail.repository.AdminOrderRepository;
import com.retail.repository.ProductRepository;
import com.retail.service.impl.AdminOrderServiceImpl;
import com.retail.vo.AdminOrderDetailVO;
import com.retail.vo.OrderDetailVO;
import com.retail.vo.OrderVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AdminOrderService 单元测试
 * 【重构说明】Mock对象由 AdminOrderMapper/ProductMapper 更新为
 * AdminOrderRepository/ProductRepository，适配Repository层架构。
 * 测试覆盖：订单CRUD、状态流转、订单统计
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminOrderService 单元测试")
class AdminOrderServiceTest {

    @Mock
    private AdminOrderRepository adminOrderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AdminOrderServiceImpl adminOrderService;

    private Order testOrder;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD123456");
        testOrder.setUserId(1L);
        testOrder.setProductId(100L);
        testOrder.setProductName("测试商品");
        testOrder.setProductPrice(new BigDecimal("99.00"));
        testOrder.setQuantity(1);
        testOrder.setTotalAmount(new BigDecimal("99.00"));
        testOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        testOrder.setPayMethod(1); // 支付宝
        testOrder.setCreateTime(LocalDateTime.now());

        testProduct = new Product();
        testProduct.setId(100L);
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.00"));
        testProduct.setStock(100);
    }

    // ==================== 订单查询测试 ====================

    @Nested
    @DisplayName("订单查询功能")
    class OrderQueryTests {

        @Test
        @DisplayName("根据ID获取订单详情 - 成功")
        void getOrderById_Success() {
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);
            when(productRepository.selectById(100L)).thenReturn(testProduct);

            OrderDetailVO result = adminOrderService.getOrderById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(100L, result.getProductId());
            verify(adminOrderRepository).selectById(1L);
        }

        @Test
        @DisplayName("根据ID获取订单详情 - 订单不存在")
        void getOrderById_NotFound() {
            when(adminOrderRepository.selectById(999L)).thenReturn(null);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminOrderService.getOrderById(999L));
            assertEquals("订单不存在", ex.getMessage());
        }

        @Test
        @DisplayName("获取管理员订单详情VO - 成功")
        void getAdminOrderDetail_Success() {
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

            AdminOrderDetailVO result = adminOrderService.getAdminOrderDetail(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("ORD123456", result.getOrderNo());
            verify(adminOrderRepository).selectById(1L);
        }

        @Test
        @DisplayName("搜索订单 - 按订单号搜索")
        void searchOrders_ByOrderNo() {
            List<Order> mockOrders = Collections.singletonList(testOrder);
            when(adminOrderRepository.selectList(any(LambdaQueryWrapper.class))).thenReturn(mockOrders);

            List<Order> result = adminOrderService.searchOrders("ORD123", null);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("ORD123456", result.get(0).getOrderNo());
        }

        @Test
        @DisplayName("获取所有订单")
        void getAllOrders() {
            when(adminOrderRepository.selectList(isNull())).thenReturn(Collections.singletonList(testOrder));

            List<Order> result = adminOrderService.getAllOrders();

            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }

    // ==================== 订单状态流转测试 ====================

    @Nested
    @DisplayName("订单状态流转")
    class OrderStatusFlowTests {

        @Test
        @DisplayName("确认支付 - 待支付→已支付")
        void confirmPayment_Success() {
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

            adminOrderService.confirmPayment(1L);

            verify(adminOrderRepository).updateById(argThat(order ->
                    order.getStatus() == OrderStatusEnum.PAID.getCode()));
        }

        @Test
        @DisplayName("确认支付 - 状态不正确时抛异常")
        void confirmPayment_WrongStatus() {
            testOrder.setStatus(OrderStatusEnum.PAID.getCode());
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminOrderService.confirmPayment(1L));
            assertEquals("订单状态不正确，无法确认支付", ex.getMessage());
        }

        @Test
        @DisplayName("发货 - 已支付→已发货")
        void shipOrder_Success() {
            testOrder.setStatus(OrderStatusEnum.PAID.getCode());
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

            adminOrderService.shipOrder(1L);

            verify(adminOrderRepository).updateById(argThat(order ->
                    order.getStatus() == OrderStatusEnum.SHIPPED.getCode()));
        }

        @Test
        @DisplayName("完成订单 - 已发货→已完成")
        void completeOrder_Success() {
            testOrder.setStatus(OrderStatusEnum.SHIPPED.getCode());
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

            adminOrderService.completeOrder(1L);

            verify(adminOrderRepository).updateById(argThat(order ->
                    order.getStatus() == OrderStatusEnum.COMPLETED.getCode()));
        }

        @Test
        @DisplayName("取消订单 - 恢复库存")
        void cancelOrder_RestoreStock() {
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);
            when(productRepository.selectById(100L)).thenReturn(testProduct);

            adminOrderService.cancelOrder(1L);

            verify(productRepository).updateById(argThat(product ->
                    product.getStock() == 101)); // 100 + 1
            verify(adminOrderRepository).updateById(argThat(order ->
                    order.getStatus() == OrderStatusEnum.CANCELLED.getCode()));
        }

        @Test
        @DisplayName("退款 - 已支付→已退款，恢复库存")
        void refundOrder_Success() {
            testOrder.setStatus(OrderStatusEnum.PAID.getCode());
            when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);
            when(productRepository.selectById(100L)).thenReturn(testProduct);

            adminOrderService.refundOrder(1L);

            verify(productRepository).updateById(any());
            verify(adminOrderRepository).updateById(argThat(order ->
                    order.getStatus() == OrderStatusEnum.REFUNDED.getCode()));
        }
    }

    // ==================== 订单删除测试 ====================

    @Test
    @DisplayName("删除订单 - 成功")
    void deleteOrder_Success() {
        when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

        adminOrderService.deleteOrder(1L);

        verify(adminOrderRepository).deleteById(1L);
    }

    // ==================== 订单统计测试 ====================

    @Test
    @DisplayName("获取订单统计信息 - 基本统计（已知MyBatis-Plus Lambda缓存限制）")
    @Disabled("MyBatis-Plus LambdaQueryWrapper在纯Mockito环境下无法解析lambda缓存，需集成测试验证")
    void getOrderStatistics() {
        // 此测试需要Spring Boot集成测试环境（@SpringBootTest）才能正常运行
        // MyBatis-Plus的LambdaQueryWrapper依赖运行时lambda序列化缓存
        when(adminOrderRepository.selectCount(isNull())).thenReturn(100L);
        when(adminOrderRepository.selectCount(any(LambdaQueryWrapper.class))).thenReturn(5L);
        when(adminOrderRepository.selectObjs(any(LambdaQueryWrapper.class)))
                .thenReturn(Collections.singletonList(new BigDecimal("999.00")));

        Map<String, Object> statistics = adminOrderService.getOrderStatistics();

        assertNotNull(statistics);
        assertEquals(100L, statistics.get("totalOrders"));
        assertNotNull(statistics.get("statusCount"));
        assertNotNull(statistics.get("todayOrders"));
        assertNotNull(statistics.get("monthOrders"));
        assertNotNull(statistics.get("totalSales"));
    }

    // ==================== 订单更新测试 ====================

    @Test
    @DisplayName("管理员更新订单 - 更新基本信息")
    void adminUpdateOrder_BasicInfo() {
        when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("orderNo", "ORD-UPDATED");
        updateData.put("receiverName", "张三");
        updateData.put("phone", "13800138000");
        updateData.put("address", "北京市朝阳区");

        when(adminOrderRepository.updateById(any())).thenReturn(1);
        when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

        Order result = adminOrderService.adminUpdateOrder(1L, updateData);

        assertNotNull(result);
        verify(adminOrderRepository).updateById(any(Order.class));
    }

    @Test
    @DisplayName("管理员更新订单 - 更新状态")
    void adminUpdateOrder_Status() {
        when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("status", "PAID");

        when(adminOrderRepository.updateById(any())).thenReturn(1);
        when(adminOrderRepository.selectById(1L)).thenReturn(testOrder);

        Order result = adminOrderService.adminUpdateOrder(1L, updateData);

        assertNotNull(result);
        verify(adminOrderRepository).updateById(any(Order.class));
    }

    @Test
    @DisplayName("管理员更新订单 - 订单不存在")
    void adminUpdateOrder_NotFound() {
        when(adminOrderRepository.selectById(999L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> adminOrderService.adminUpdateOrder(999L, new HashMap<>()));
        assertEquals("订单不存在", ex.getMessage());
    }
}
