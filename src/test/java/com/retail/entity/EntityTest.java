package com.retail.entity;

import com.retail.common.OrderStatusEnum;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Entity 实体类测试")
class EntityTest {

    // ==================== User 实体测试 ====================

    @Nested
    @DisplayName("User 实体")
    class UserEntityTests {

        @Test
        @DisplayName("getter/setter 完整测试")
        void testGettersAndSetters() {
            User user = new User();
            LocalDateTime now = LocalDateTime.now();

            user.setId(1L);
            user.setUsername("testuser");
            user.setPassword("encrypted");
            user.setPhone("13800138000");
            user.setEmail("test@example.com");
            user.setUserType(0);
            user.setStatus(1);
            user.setRealName("测试用户");
            user.setAvatar("avatar.png");
            user.setVipExpireTime(now);
            user.setCreateTime(now);
            user.setUpdateTime(now);
            user.setDeleted(0);

            assertEquals(1L, user.getId());
            assertEquals("testuser", user.getUsername());
            assertEquals("encrypted", user.getPassword());
            assertEquals("13800138000", user.getPhone());
            assertEquals("test@example.com", user.getEmail());
            assertEquals(0, user.getUserType());
            assertEquals(1, user.getStatus());
            assertEquals("测试用户", user.getRealName());
            assertEquals("avatar.png", user.getAvatar());
            assertEquals(now, user.getVipExpireTime());
            assertEquals(now, user.getCreateTime());
            assertEquals(now, user.getUpdateTime());
            assertEquals(0, user.getDeleted());
        }
    }

    // ==================== Product 实体测试 ====================

    @Nested
    @DisplayName("Product 实体")
    class ProductEntityTests {

        @Test
        @DisplayName("getter/setter 完整测试")
        void testGettersAndSetters() {
            Product product = new Product();
            LocalDateTime now = LocalDateTime.now();

            product.setId(1L);
            product.setName("测试商品");
            product.setCode("PROD001");
            product.setDescription("商品描述");
            product.setPrice(new BigDecimal("99.00"));
            product.setStock(100);
            product.setImageUrl("image.jpg");
            product.setCategory(1);
            product.setStatus(1);
            product.setSalesCount(50);
            product.setCreateBy(1L);
            product.setCreateTime(now);
            product.setUpdateTime(now);
            product.setDeleted(0);

            assertEquals(1L, product.getId());
            assertEquals("测试商品", product.getName());
            assertEquals("PROD001", product.getCode());
            assertEquals("商品描述", product.getDescription());
            assertEquals(new BigDecimal("99.00"), product.getPrice());
            assertEquals(100, product.getStock());
            assertEquals("image.jpg", product.getImageUrl());
            assertEquals(1, product.getCategory());
            assertEquals(1, product.getStatus());
            assertEquals(50, product.getSalesCount());
            assertEquals(50, product.getSales());
            assertEquals(1L, product.getCreateBy());
            assertEquals(now, product.getCreateTime());
            assertEquals(now, product.getUpdateTime());
            assertEquals(0, product.getDeleted());
        }

        @Test
        @DisplayName("getSales/setSales 兼容方法")
        void testSalesCompatibility() {
            Product product = new Product();
            product.setSales(100);
            assertEquals(100, product.getSalesCount());
            assertEquals(100, product.getSales());
        }

        @Test
        @DisplayName("getCategoryName - 所有分类")
        void testCategoryName() {
            Product p = new Product();
            p.setCategory(1);
            assertEquals("电子产品", p.getCategoryName());
            p.setCategory(2);
            assertEquals("服装", p.getCategoryName());
            p.setCategory(3);
            assertEquals("食品", p.getCategoryName());
            p.setCategory(4);
            assertEquals("图书", p.getCategoryName());
            p.setCategory(5);
            assertEquals("其他", p.getCategoryName());
            p.setCategory(999);
            assertEquals("未知", p.getCategoryName());
        }

        @Test
        @DisplayName("getStatusName - 所有状态")
        void testStatusName() {
            Product p = new Product();
            p.setStatus(0);
            assertEquals("下架", p.getStatusName());
            p.setStatus(1);
            assertEquals("上架", p.getStatusName());
            p.setStatus(999);
            assertEquals("未知", p.getStatusName());
        }
    }

    // ==================== AdminUser 实体测试 ====================

    @Nested
    @DisplayName("AdminUser 实体")
    class AdminUserEntityTests {

        @Test
        @DisplayName("getter/setter 完整测试")
        void testGettersAndSetters() {
            AdminUser admin = new AdminUser();
            LocalDateTime now = LocalDateTime.now();

            admin.setId(1L);
            admin.setUsername("admin");
            admin.setPassword("pass");
            admin.setRealName("管理员");
            admin.setEmail("admin@test.com");
            admin.setPhone("13900139000");
            admin.setRole("ADMIN");
            admin.setStatus(1);
            admin.setLastLoginTime(now);
            admin.setLastLoginIp("127.0.0.1");
            admin.setCreatedAt(now);
            admin.setUpdatedAt(now);
            admin.setDeleted(0);

            assertEquals(1L, admin.getId());
            assertEquals("admin", admin.getUsername());
            assertEquals("pass", admin.getPassword());
            assertEquals("管理员", admin.getRealName());
            assertEquals("admin@test.com", admin.getEmail());
            assertEquals("13900139000", admin.getPhone());
            assertEquals("ADMIN", admin.getRole());
            assertEquals(1, admin.getStatus());
            assertEquals(now, admin.getLastLoginTime());
            assertEquals("127.0.0.1", admin.getLastLoginIp());
            assertEquals(now, admin.getCreatedAt());
            assertEquals(now, admin.getUpdatedAt());
            assertEquals(0, admin.getDeleted());
        }
    }

    // ==================== Order 实体测试 ====================

    @Nested
    @DisplayName("Order 实体")
    class OrderEntityTests {

        @Test
        @DisplayName("getter/setter 完整测试")
        void testGettersAndSetters() {
            Order order = new Order();
            LocalDateTime now = LocalDateTime.now();

            order.setId(1L);
            order.setOrderNo("ORD001");
            order.setUserId(100L);
            order.setProductId(200L);
            order.setProductName("商品A");
            order.setProductPrice(new BigDecimal("50.00"));
            order.setQuantity(2);
            order.setTotalAmount(new BigDecimal("100.00"));
            order.setPayAmount(new BigDecimal("90.00"));
            order.setStatus(0);
            order.setPayMethod(1);
            order.setPayTime(now);
            order.setAddress("北京市");
            order.setPhone("13800138000");
            order.setReceiverName("张三");
            order.setRemark("备注");
            order.setCreateTime(now);
            order.setUpdateTime(now);
            order.setDeleted(0);

            assertEquals(1L, order.getId());
            assertEquals("ORD001", order.getOrderNo());
            assertEquals(100L, order.getUserId());
            assertEquals(200L, order.getProductId());
            assertEquals("商品A", order.getProductName());
            assertEquals(new BigDecimal("50.00"), order.getProductPrice());
            assertEquals(2, order.getQuantity());
            assertEquals(new BigDecimal("100.00"), order.getTotalAmount());
            assertEquals(new BigDecimal("90.00"), order.getPayAmount());
            assertEquals(0, order.getStatus());
            assertEquals(1, order.getPayMethod());
            assertEquals(now, order.getPayTime());
            assertEquals("北京市", order.getAddress());
            assertEquals("13800138000", order.getPhone());
            assertEquals("张三", order.getReceiverName());
            assertEquals("备注", order.getRemark());
            assertEquals(now, order.getCreateTime());
            assertEquals(now, order.getUpdateTime());
            assertEquals(0, order.getDeleted());
        }

        @Test
        @DisplayName("getStatusName - 使用 OrderStatusEnum")
        void testStatusName() {
            Order order = new Order();
            order.setStatus(0);
            assertEquals("待支付", order.getStatusName());
            order.setStatus(1);
            assertEquals("已支付", order.getStatusName());
            order.setStatus(2);
            assertEquals("已发货", order.getStatusName());
            order.setStatus(3);
            assertEquals("已完成", order.getStatusName());
            order.setStatus(4);
            assertEquals("已取消", order.getStatusName());
            order.setStatus(5);
            assertEquals("已退款", order.getStatusName());
        }

        @Test
        @DisplayName("getPayMethodName - 所有支付方式")
        void testPayMethodName() {
            Order order = new Order();
            order.setPayMethod(1);
            assertEquals("支付宝", order.getPayMethodName());
            order.setPayMethod(2);
            assertEquals("微信", order.getPayMethodName());
            order.setPayMethod(3);
            assertEquals("银行卡", order.getPayMethodName());
            order.setPayMethod(999);
            assertEquals("未知", order.getPayMethodName());
        }
    }
}
