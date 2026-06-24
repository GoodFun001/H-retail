package com.retail.controller;

import com.retail.common.Result;
import com.retail.entity.Order;
import com.retail.entity.User;
import com.retail.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("更多Controller测试")
class MoreControllerTest {

    @Nested
    @DisplayName("OrderController")
    class OrderControllerTests {

        @Mock
        private OrderService orderService;

        @Mock
        private Authentication authentication;

        @Mock
        private SecurityContext securityContext;

        @InjectMocks
        private OrderController orderController;

        private MockHttpServletRequest request;
        private User mockUser;
        private Order testOrder;

        @BeforeEach
        void setUp() {
            request = new MockHttpServletRequest();
            mockUser = new User();
            mockUser.setId(1L);
            mockUser.setUsername("testuser");

            testOrder = new Order();
            testOrder.setId(1L);
            testOrder.setUserId(1L);
            testOrder.setProductId(100L);
            testOrder.setQuantity(2);
            testOrder.setTotalAmount(new BigDecimal("198.00"));
            testOrder.setStatus(0);

            SecurityContextHolder.setContext(securityContext);
            lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
            lenient().when(authentication.getPrincipal()).thenReturn(mockUser);
        }

        @AfterEach
        void tearDown() {
            SecurityContextHolder.clearContext();
        }

        @Test
        @DisplayName("createOrder - 成功")
        void createOrder_Success() {
            when(orderService.createOrder(any(Order.class))).thenReturn(testOrder);
            Result<Order> result = orderController.createOrder(testOrder, request);
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
        }

        @Test
        @DisplayName("createOrder - 未登录")
        void createOrder_Unauthorized() {
            when(authentication.getPrincipal()).thenReturn(null);
            Result<Order> result = orderController.createOrder(testOrder, request);
            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("getUserOrders - 成功")
        void getUserOrders_Success() {
            when(orderService.getOrdersByUserId(1L)).thenReturn(Arrays.asList(testOrder));
            Result<List<Order>> result = orderController.getUserOrders(request);
            assertEquals(200, result.getCode());
            assertEquals(1, result.getData().size());
        }

        @Test
        @DisplayName("getOrderById - 成功")
        void getOrderById_Success() {
            when(orderService.getOrderById(1L)).thenReturn(testOrder);
            Result<Order> result = orderController.getOrderById(1L, request);
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("getOrderById - 未登录")
        void getOrderById_Unauthorized() {
            when(authentication.getPrincipal()).thenReturn(null);
            Result<Order> result = orderController.getOrderById(1L, request);
            assertEquals(401, result.getCode());
        }

        @Test
        @DisplayName("cancelOrder - 成功")
        void cancelOrder_Success() {
            when(orderService.cancelOrder(1L)).thenReturn(true);
            Result<String> result = orderController.cancelOrder(1L, request);
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("confirmOrder - 成功")
        void confirmOrder_Success() {
            when(orderService.updateOrderStatus(1L, 3)).thenReturn(true);
            Result<String> result = orderController.confirmOrder(1L, request);
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("getOrderStatistics - 成功")
        void getOrderStatistics_Success() {
            Result<java.util.Map<String, Object>> result = orderController.getOrderStatistics(request);
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
        }
    }
}
