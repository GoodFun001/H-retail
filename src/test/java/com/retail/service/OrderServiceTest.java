package com.retail.service;

import com.retail.entity.Order;
import com.retail.entity.Product;
import com.retail.repository.OrderRepository;
import com.retail.service.impl.OrderServiceImpl;
import com.retail.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService 测试")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;
    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(100L);
        product.setName("测试商品");
        product.setPrice(new BigDecimal("99.00"));
        product.setStock(50);
        product.setSalesCount(10);

        order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD001");
        order.setUserId(1L);
        order.setProductId(100L);
        order.setQuantity(2);
        order.setTotalAmount(new BigDecimal("198.00"));
        order.setStatus(0);
    }

    @Nested
    @DisplayName("创建订单")
    class CreateOrder {

        @Test
        @DisplayName("创建订单成功")
        void createOrder_Success() {
            when(productService.getProductDetail(100L)).thenReturn(product);
            when(redisUtil.getString("product:stock:100")).thenReturn("50");
            when(redisUtil.deductStock("product:stock:100", 2)).thenReturn(48L);
            when(orderRepository.insert(any(Order.class))).thenReturn(1);
            when(productService.updateById(any(Product.class))).thenReturn(true);

            Order result = orderService.createOrder(order);
            assertNotNull(result);
            assertNotNull(result.getOrderNo());
            assertEquals(0, result.getStatus());
            assertEquals("测试商品", result.getProductName());
        }

        @Test
        @DisplayName("创建订单失败 - 商品不存在")
        void createOrder_ProductNotFound() {
            when(productService.getProductDetail(100L)).thenReturn(null);
            assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
        }

        @Test
        @DisplayName("创建订单失败 - 数量无效")
        void createOrder_InvalidQuantity() {
            order.setQuantity(null);
            when(productService.getProductDetail(100L)).thenReturn(product);
            assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
        }

        @Test
        @DisplayName("创建订单失败 - 库存不足")
        void createOrder_InsufficientStock() {
            when(productService.getProductDetail(100L)).thenReturn(product);
            when(redisUtil.getString("product:stock:100")).thenReturn("50");
            when(redisUtil.deductStock("product:stock:100", 2)).thenReturn(-1L);

            assertThrows(RuntimeException.class, () -> orderService.createOrder(order));
            // addStock 在代码中被调用了两次：一次在库存不足检查(67行)，一次在异常catch(105行)
            verify(redisUtil, atLeast(1)).addStock("product:stock:100", 2);
        }
    }

    @Nested
    @DisplayName("订单查询")
    class OrderQuery {

        @Test
        @DisplayName("getOrderById")
        void getOrderById() {
            when(orderRepository.findById(1L)).thenReturn(order);
            Order result = orderService.getOrderById(1L);
            assertNotNull(result);
            assertEquals("ORD001", result.getOrderNo());
        }

        @Test
        @DisplayName("getOrdersByUserId")
        void getOrdersByUserId() {
            when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order));
            List<Order> result = orderService.getOrdersByUserId(1L);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("getAllOrders")
        void getAllOrders() {
            when(orderRepository.findAll()).thenReturn(Arrays.asList(order));
            List<Order> result = orderService.getAllOrders();
            assertEquals(1, result.size());
        }
    }

    @Nested
    @DisplayName("订单状态更新")
    class OrderStatus {

        @Test
        @DisplayName("updateOrderStatus")
        void updateOrderStatus() {
            when(orderRepository.updateStatus(1L, 1)).thenReturn(1);
            assertTrue(orderService.updateOrderStatus(1L, 1));
        }

        @Test
        @DisplayName("updateOrderStatus - 失败")
        void updateOrderStatus_Fail() {
            when(orderRepository.updateStatus(1L, 1)).thenReturn(0);
            assertFalse(orderService.updateOrderStatus(1L, 1));
        }

        @Test
        @DisplayName("cancelOrder")
        void cancelOrder() {
            when(orderRepository.updateStatus(1L, 2)).thenReturn(1);
            assertTrue(orderService.cancelOrder(1L));
        }
    }
}
