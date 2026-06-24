package com.retail.service;

import com.retail.common.OrderStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderStatusEnum 单元测试
 * 测试覆盖：状态码解析、状态名映射、边界情况
 */
@DisplayName("OrderStatusEnum 单元测试")
class OrderStatusEnumTest {

    @Nested
    @DisplayName("状态码解析")
    class CodeResolution {

        @ParameterizedTest
        @DisplayName("resolveCode - 有效状态名称")
        @CsvSource({
                "PENDING, 0",
                "PENDING_PAYMENT, 0",
                "PAID, 1",
                "PROCESSING, 1",
                "SHIPPED, 2",
                "COMPLETED, 3",
                "DELIVERED, 3",
                "CANCELLED, 4",
                "REFUNDED, 5"
        })
        void resolveCode_ValidNames(String input, Integer expectedCode) {
            assertEquals(expectedCode, OrderStatusEnum.resolveCode(input));
        }

        @ParameterizedTest
        @DisplayName("resolveCode - 数字字符串")
        @CsvSource({
                "0, 0",
                "1, 1",
                "2, 2",
                "3, 3",
                "4, 4",
                "5, 5"
        })
        void resolveCode_NumericStrings(String input, Integer expectedCode) {
            assertEquals(expectedCode, OrderStatusEnum.resolveCode(input));
        }

        @Test
        @DisplayName("resolveCode - null输入")
        void resolveCode_NullInput() {
            assertNull(OrderStatusEnum.resolveCode(null));
        }

        @Test
        @DisplayName("resolveCode - 无效状态名称")
        void resolveCode_InvalidName() {
            assertNull(OrderStatusEnum.resolveCode("INVALID_STATUS"));
        }
    }

    @Nested
    @DisplayName("状态码转枚举")
    class FromCode {

        @ParameterizedTest
        @CsvSource({
                "0, PENDING_PAYMENT",
                "1, PAID",
                "2, SHIPPED",
                "3, COMPLETED",
                "4, CANCELLED",
                "5, REFUNDED"
        })
        @DisplayName("fromCode - 有效状态码")
        void fromCode_Valid(int code, String expectedName) {
            OrderStatusEnum status = OrderStatusEnum.fromCode(code);
            assertNotNull(status);
            assertEquals(expectedName, status.name());
        }

        @Test
        @DisplayName("fromCode - 无效状态码")
        void fromCode_Invalid() {
            assertNull(OrderStatusEnum.fromCode(99));
            assertNull(OrderStatusEnum.fromCode(-1));
        }
    }

    @Nested
    @DisplayName("状态描述")
    class Description {

        @ParameterizedTest
        @CsvSource({
                "0, 待支付",
                "1, 已支付",
                "2, 已发货",
                "3, 已完成",
                "4, 已取消",
                "5, 已退款"
        })
        @DisplayName("getDescByCode - 返回正确的中文描述")
        void getDescByCode(int code, String expectedDesc) {
            assertEquals(expectedDesc, OrderStatusEnum.getDescByCode(code));
        }

        @Test
        @DisplayName("getDescByCode - 无效状态码返回'未知'")
        void getDescByCode_Invalid() {
            assertEquals("未知", OrderStatusEnum.getDescByCode(99));
        }

        @Test
        @DisplayName("每个枚举值的getDesc方法")
        void enumDesc() {
            assertEquals("待支付", OrderStatusEnum.PENDING_PAYMENT.getDesc());
            assertEquals("已支付", OrderStatusEnum.PAID.getDesc());
            assertEquals("已发货", OrderStatusEnum.SHIPPED.getDesc());
            assertEquals("已完成", OrderStatusEnum.COMPLETED.getDesc());
            assertEquals("已取消", OrderStatusEnum.CANCELLED.getDesc());
            assertEquals("已退款", OrderStatusEnum.REFUNDED.getDesc());
        }
    }

    @Nested
    @DisplayName("状态名映射")
    class FromName {

        @ParameterizedTest
        @CsvSource({
                "pending_payment, PENDING_PAYMENT",
                "paid, PAID",
                "shipped, SHIPPED",
                "completed, COMPLETED",
                "cancelled, CANCELLED",
                "refunded, REFUNDED"
        })
        @DisplayName("fromName - 小写名称")
        void fromName_Lowercase(String name, String expectedEnum) {
            OrderStatusEnum status = OrderStatusEnum.fromName(name);
            assertNotNull(status);
            assertEquals(expectedEnum, status.name());
        }

        @Test
        @DisplayName("fromName - null输入")
        void fromName_Null() {
            assertNull(OrderStatusEnum.fromName(null));
        }
    }

    @Test
    @DisplayName("values() 应包含6个状态")
    void allValues() {
        assertEquals(6, OrderStatusEnum.values().length);
    }
}
