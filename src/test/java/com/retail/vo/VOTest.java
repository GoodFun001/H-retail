package com.retail.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("VO 视图对象测试")
class VOTest {

    @Test
    @DisplayName("OrderVO getter/setter")
    void testOrderVO() {
        OrderVO vo = new OrderVO();
        LocalDateTime now = LocalDateTime.now();

        vo.setId(1L);
        vo.setUserId(100L);
        vo.setTotalPrice(new BigDecimal("299.00"));
        vo.setStatus(1);
        vo.setStatusText("已支付");
        vo.setShippingAddress("北京市朝阳区");
        vo.setPaymentMethod("支付宝");
        vo.setCreateTime(now);
        vo.setUpdateTime(now);

        OrderDetailVO detail = new OrderDetailVO();
        detail.setId(10L);
        detail.setOrderId(1L);
        detail.setProductId(200L);
        detail.setProductName("测试商品");
        detail.setProductImage("img.png");
        detail.setPrice(new BigDecimal("99.00"));
        detail.setQuantity(3);
        detail.setSubtotal(new BigDecimal("297.00"));

        vo.setOrderDetails(Collections.singletonList(detail));

        assertEquals(1L, vo.getId());
        assertEquals(100L, vo.getUserId());
        assertEquals(new BigDecimal("299.00"), vo.getTotalPrice());
        assertEquals(1, vo.getStatus());
        assertEquals("已支付", vo.getStatusText());
        assertEquals("北京市朝阳区", vo.getShippingAddress());
        assertEquals("支付宝", vo.getPaymentMethod());
        assertEquals(now, vo.getCreateTime());
        assertEquals(now, vo.getUpdateTime());
        assertNotNull(vo.getOrderDetails());
        assertEquals(1, vo.getOrderDetails().size());
        assertEquals("测试商品", vo.getOrderDetails().get(0).getProductName());
    }

    @Test
    @DisplayName("OrderDetailVO getter/setter")
    void testOrderDetailVO() {
        OrderDetailVO vo = new OrderDetailVO();

        vo.setId(1L);
        vo.setOrderId(100L);
        vo.setProductId(200L);
        vo.setProductName("商品");
        vo.setProductImage("img.jpg");
        vo.setPrice(new BigDecimal("50.00"));
        vo.setQuantity(2);
        vo.setSubtotal(new BigDecimal("100.00"));

        assertEquals(1L, vo.getId());
        assertEquals(100L, vo.getOrderId());
        assertEquals(200L, vo.getProductId());
        assertEquals("商品", vo.getProductName());
        assertEquals("img.jpg", vo.getProductImage());
        assertEquals(new BigDecimal("50.00"), vo.getPrice());
        assertEquals(2, vo.getQuantity());
        assertEquals(new BigDecimal("100.00"), vo.getSubtotal());
    }
}
