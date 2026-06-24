package com.retail.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retail.common.OrderStatusEnum;
import com.retail.config.JwtAuthenticationFilter;
import com.retail.entity.Order;
import com.retail.service.AdminOrderService;
import com.retail.util.JwtUtil;
import com.retail.vo.AdminOrderDetailVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AdminOrderController Web层测试
 * 测试覆盖：HTTP端点、请求参数解析、响应格式
 * 使用 addFilters=false 禁用Spring Security过滤器以便测试Controller逻辑
 */
@WebMvcTest(AdminOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminOrderController Web层测试")
class AdminOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminOrderService adminOrderService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private Order testOrder;
    private AdminOrderDetailVO testDetailVO;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD123456");
        testOrder.setUserId(1L);
        testOrder.setProductId(100L);
        testOrder.setProductName("测试商品");
        testOrder.setTotalAmount(new BigDecimal("99.00"));
        testOrder.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        testOrder.setPayMethod(1); // 支付宝
        testOrder.setCreateTime(LocalDateTime.now());

        testDetailVO = AdminOrderDetailVO.fromOrder(testOrder);
    }

    @Nested
    @DisplayName("GET /admin/orders - 订单列表")
    class GetOrderList {

        @Test
        @DisplayName("获取订单列表 - 成功返回200")
        void getOrderList_Success() throws Exception {
            when(adminOrderService.getAllOrders()).thenReturn(Collections.singletonList(testOrder));

            mockMvc.perform(get("/admin/orders")
                            .param("current", "1")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.records").isArray())
                    .andExpect(jsonPath("$.data.total").isNumber());
        }

        @Test
        @DisplayName("获取订单列表 - 默认分页参数")
        void getOrderList_DefaultParams() throws Exception {
            when(adminOrderService.getAllOrders()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/admin/orders"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("GET /admin/orders/{id} - 订单详情")
    class GetOrderDetail {

        @Test
        @DisplayName("获取订单详情 - 成功返回200")
        void getOrderDetail_Success() throws Exception {
            when(adminOrderService.getAdminOrderDetail(1L)).thenReturn(testDetailVO);

            mockMvc.perform(get("/admin/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.orderNo").value("ORD123456"));
        }

        @Test
        @DisplayName("获取订单详情 - 订单不存在返回404")
        void getOrderDetail_NotFound() throws Exception {
            when(adminOrderService.getAdminOrderDetail(999L))
                    .thenThrow(new RuntimeException("订单不存在"));

            mockMvc.perform(get("/admin/orders/999"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404))
                    .andExpect(jsonPath("$.msg").value("订单不存在"));
        }
    }

    @Nested
    @DisplayName("GET /admin/orders/search - 搜索订单")
    class SearchOrders {

        @Test
        @DisplayName("搜索订单 - 按订单号")
        void searchOrders_ByOrderNo() throws Exception {
            when(adminOrderService.searchOrders(eq("ORD123"), isNull()))
                    .thenReturn(Collections.singletonList(testOrder));

            mockMvc.perform(get("/admin/orders/search")
                            .param("orderNo", "ORD123"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray());
        }

        @Test
        @DisplayName("搜索订单 - 无参数")
        void searchOrders_NoParams() throws Exception {
            when(adminOrderService.searchOrders(isNull(), isNull()))
                    .thenReturn(Collections.emptyList());

            mockMvc.perform(get("/admin/orders/search"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("PUT /admin/orders/{id} - 更新订单")
    class UpdateOrder {

        @Test
        @DisplayName("更新订单 - 成功")
        void updateOrder_Success() throws Exception {
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("status", "PAID");

            when(adminOrderService.adminUpdateOrder(eq(1L), any(Map.class)))
                    .thenReturn(testOrder);

            mockMvc.perform(put("/admin/orders/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateData)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("更新订单 - 订单不存在")
        void updateOrder_NotFound() throws Exception {
            when(adminOrderService.adminUpdateOrder(eq(999L), any(Map.class)))
                    .thenThrow(new RuntimeException("订单不存在"));

            mockMvc.perform(put("/admin/orders/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    @Nested
    @DisplayName("POST /admin/orders/{id}/ship - 发货")
    class ShipOrder {

        @Test
        @DisplayName("发货 - 成功")
        void shipOrder_Success() throws Exception {
            doNothing().when(adminOrderService).shipOrder(1L);

            mockMvc.perform(post("/admin/orders/1/ship"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("发货成功"));
        }
    }

    @Nested
    @DisplayName("POST /admin/orders/{id}/refund - 退款")
    class RefundOrder {

        @Test
        @DisplayName("退款 - 成功")
        void refundOrder_Success() throws Exception {
            doNothing().when(adminOrderService).refundOrder(1L);

            mockMvc.perform(post("/admin/orders/1/refund"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("退款成功"));
        }
    }

    @Nested
    @DisplayName("DELETE /admin/orders/{id} - 删除订单")
    class DeleteOrder {

        @Test
        @DisplayName("删除订单 - 成功")
        void deleteOrder_Success() throws Exception {
            doNothing().when(adminOrderService).deleteOrder(1L);

            mockMvc.perform(delete("/admin/orders/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("GET /admin/orders/statistics - 订单统计")
    class OrderStatistics {

        @Test
        @DisplayName("获取订单统计 - 成功")
        void getOrderStatistics_Success() throws Exception {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalOrders", 100L);
            stats.put("todayOrders", 5L);

            when(adminOrderService.getOrderStatistics()).thenReturn(stats);

            mockMvc.perform(get("/admin/orders/statistics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.totalOrders").value(100));
        }
    }
}
