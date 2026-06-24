package com.retail.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OrderStatusEnum 补充测试")
class OrderStatusEnumTest {

    @Nested
    @DisplayName("resolveCode - 字符串解析")
    class ResolveCode {

        @Test
        @DisplayName("纯数字字符串")
        void resolveCode_Numeric() {
            assertEquals(0, OrderStatusEnum.resolveCode("0"));
            assertEquals(1, OrderStatusEnum.resolveCode("1"));
            assertEquals(5, OrderStatusEnum.resolveCode("5"));
        }

        @Test
        @DisplayName("兼容旧状态名")
        void resolveCode_LegacyNames() {
            assertEquals(0, OrderStatusEnum.resolveCode("PENDING"));
            assertEquals(0, OrderStatusEnum.resolveCode("PENDING_PAYMENT"));
            assertEquals(1, OrderStatusEnum.resolveCode("PAID"));
            assertEquals(1, OrderStatusEnum.resolveCode("PROCESSING"));
            assertEquals(2, OrderStatusEnum.resolveCode("SHIPPED"));
            assertEquals(3, OrderStatusEnum.resolveCode("COMPLETED"));
            assertEquals(3, OrderStatusEnum.resolveCode("DELIVERED"));
            assertEquals(4, OrderStatusEnum.resolveCode("CANCELLED"));
            assertEquals(5, OrderStatusEnum.resolveCode("REFUNDED"));
        }

        @Test
        @DisplayName("null输入")
        void resolveCode_Null() {
            assertNull(OrderStatusEnum.resolveCode(null));
        }

        @Test
        @DisplayName("无效状态名")
        void resolveCode_Invalid() {
            assertNull(OrderStatusEnum.resolveCode("INVALID_STATUS"));
        }
    }

    @Test
    @DisplayName("fromCode - 无效code返回null")
    void fromCode_Invalid() {
        assertNull(OrderStatusEnum.fromCode(999));
    }

    @Test
    @DisplayName("fromName - 大小写不敏感")
    void fromName_CaseInsensitive() {
        assertEquals(OrderStatusEnum.PAID, OrderStatusEnum.fromName("paid"));
        assertEquals(OrderStatusEnum.PAID, OrderStatusEnum.fromName("PAID"));
    }

    @Test
    @DisplayName("fromName - null返回null")
    void fromName_Null() {
        assertNull(OrderStatusEnum.fromName(null));
    }

    @Test
    @DisplayName("fromName - 无效名称返回null")
    void fromName_Invalid() {
        assertNull(OrderStatusEnum.fromName("INVALID"));
    }

    @Test
    @DisplayName("getDescByCode - 有效code")
    void getDescByCode_Valid() {
        assertEquals("待支付", OrderStatusEnum.getDescByCode(0));
        assertEquals("已完成", OrderStatusEnum.getDescByCode(3));
    }

    @Test
    @DisplayName("getDescByCode - 无效code返回'未知'")
    void getDescByCode_Invalid() {
        assertEquals("未知", OrderStatusEnum.getDescByCode(999));
    }

    @Test
    @DisplayName("getCode 和 getDesc - 所有枚举值")
    void allEnumValues() {
        assertEquals(0, OrderStatusEnum.PENDING_PAYMENT.getCode());
        assertEquals("待支付", OrderStatusEnum.PENDING_PAYMENT.getDesc());
        assertEquals(1, OrderStatusEnum.PAID.getCode());
        assertEquals("已支付", OrderStatusEnum.PAID.getDesc());
        assertEquals(2, OrderStatusEnum.SHIPPED.getCode());
        assertEquals("已发货", OrderStatusEnum.SHIPPED.getDesc());
        assertEquals(3, OrderStatusEnum.COMPLETED.getCode());
        assertEquals("已完成", OrderStatusEnum.COMPLETED.getDesc());
        assertEquals(4, OrderStatusEnum.CANCELLED.getCode());
        assertEquals("已取消", OrderStatusEnum.CANCELLED.getDesc());
        assertEquals(5, OrderStatusEnum.REFUNDED.getCode());
        assertEquals("已退款", OrderStatusEnum.REFUNDED.getDesc());
    }
}
