package com.retail.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("全部DTO测试")
class AllDTOTest {

    @Test
    @DisplayName("AdminLoginDTO")
    void testAdminLoginDTO() {
        AdminLoginDTO dto = new AdminLoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");
        dto.setCaptcha("1234");
        dto.setRememberMe(true);
        assertEquals("admin", dto.getUsername());
        assertEquals("123456", dto.getPassword());
        assertEquals("1234", dto.getCaptcha());
        assertTrue(dto.getRememberMe());
    }

    @Test
    @DisplayName("ProductCreateDTO")
    void testProductCreateDTO() {
        ProductCreateDTO dto = new ProductCreateDTO();
        dto.setName("商品A");
        dto.setCode("P001");
        dto.setDescription("描述");
        dto.setCategory("电子产品");
        dto.setPrice(new BigDecimal("99.00"));
        dto.setStock(100);
        dto.setImage("img.png");
        dto.setSpecifications("规格");
        dto.setBrand("品牌");
        dto.setWeight(new BigDecimal("1.5"));
        dto.setTags("标签1,标签2");
        assertEquals("商品A", dto.getName());
        assertEquals(new BigDecimal("99.00"), dto.getPrice());
        assertEquals(100, dto.getStock());
    }

    @Test
    @DisplayName("ProductUpdateDTO")
    void testProductUpdateDTO() {
        ProductUpdateDTO dto = new ProductUpdateDTO();
        dto.setName("更新商品");
        dto.setPrice(new BigDecimal("199.00"));
        dto.setStock(50);
        assertEquals("更新商品", dto.getName());
        assertEquals(new BigDecimal("199.00"), dto.getPrice());
        assertEquals(50, dto.getStock());
    }

    @Test
    @DisplayName("ProductSearchDTO")
    void testProductSearchDTO() {
        ProductSearchDTO dto = new ProductSearchDTO();
        dto.setKeyword("手机");
        dto.setCategory("1");
        dto.setMinPrice(new BigDecimal("10.00"));
        dto.setMaxPrice(new BigDecimal("1000.00"));
        dto.setInStockOnly(true);
        assertEquals("手机", dto.getKeyword());
        assertEquals(new BigDecimal("10.00"), dto.getMinPrice());
        assertTrue(dto.getInStockOnly());
    }

    @Test
    @DisplayName("OrderCreateDTO")
    void testOrderCreateDTO() {
        OrderCreateDTO dto = new OrderCreateDTO();
        dto.setAddressId(1L);
        dto.setRemarks("备注");
        dto.setCouponId(100L);
        OrderCreateDTO.OrderItemDTO item = new OrderCreateDTO.OrderItemDTO();
        item.setProductId(10L);
        item.setQuantity(2);
        dto.setItems(Arrays.asList(item));
        assertEquals(1L, dto.getAddressId());
        assertEquals("备注", dto.getRemarks());
        assertEquals(1, dto.getItems().size());
        assertEquals(10L, dto.getItems().get(0).getProductId());
    }

    @Test
    @DisplayName("OrderUpdateDTO")
    void testOrderUpdateDTO() {
        OrderUpdateDTO dto = new OrderUpdateDTO();
        dto.setId(1L);
        dto.setStatus("PAID");
        dto.setTrackingNumber("SF123456");
        assertEquals(1L, dto.getId());
        assertEquals("PAID", dto.getStatus());
        assertEquals("SF123456", dto.getTrackingNumber());
    }

    @Test
    @DisplayName("OrderSearchDTO")
    void testOrderSearchDTO() {
        OrderSearchDTO dto = new OrderSearchDTO();
        dto.setOrderNo("ORD001");
        dto.setStatus("PAID");
        dto.setUsername("testuser");
        dto.setPage(1);
        dto.setSize(10);
        assertEquals("ORD001", dto.getOrderNo());
        assertEquals("PAID", dto.getStatus());
        assertEquals("testuser", dto.getUsername());
        assertEquals(1, dto.getPage());
    }

    @Test
    @DisplayName("UserCreateDTO")
    void testUserCreateDTO() {
        UserCreateDTO dto = new UserCreateDTO();
        dto.setUsername("newuser");
        dto.setPassword("pass123");
        dto.setEmail("new@test.com");
        dto.setPhone("13800138000");
        dto.setRole("user");
        dto.setStatus(1);
        assertEquals("newuser", dto.getUsername());
        assertEquals("pass123", dto.getPassword());
        assertEquals("new@test.com", dto.getEmail());
        assertEquals(1, dto.getStatus());
    }

    @Test
    @DisplayName("UserUpdateDTO")
    void testUserUpdateDTO() {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setUsername("updated");
        dto.setEmail("updated@test.com");
        dto.setPhone("13900139000");
        dto.setStatus(0);
        assertEquals("updated", dto.getUsername());
        assertEquals("updated@test.com", dto.getEmail());
        assertEquals(0, dto.getStatus());
    }

    @Test
    @DisplayName("UserSearchDTO")
    void testUserSearchDTO() {
        UserSearchDTO dto = new UserSearchDTO();
        dto.setUsername("search");
        dto.setRole(1);
        dto.setStatus(1);
        dto.setPageNum(1);
        dto.setPageSize(10);
        assertEquals("search", dto.getUsername());
        assertEquals(1, dto.getRole());
        assertEquals(1, dto.getPageNum());
    }
}
