package com.retail.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DTO 数据传输对象测试")
class DTOTest {

    @Test
    @DisplayName("ProductDTO getter/setter")
    void testProductDTO() {
        ProductDTO dto = new ProductDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(1L);
        dto.setName("商品");
        dto.setDescription("描述");
        dto.setPrice(new BigDecimal("99.00"));
        dto.setStock(100);
        dto.setImage("img.png");
        dto.setStatus(1);
        dto.setCategory("电子产品");
        dto.setCreateTime(now);
        dto.setUpdateTime(now);

        assertEquals(1L, dto.getId());
        assertEquals("商品", dto.getName());
        assertEquals("描述", dto.getDescription());
        assertEquals(new BigDecimal("99.00"), dto.getPrice());
        assertEquals(100, dto.getStock());
        assertEquals("img.png", dto.getImage());
        assertEquals(1, dto.getStatus());
        assertEquals("电子产品", dto.getCategory());
        assertEquals(now, dto.getCreateTime());
        assertEquals(now, dto.getUpdateTime());
    }

    @Test
    @DisplayName("UserDTO getter/setter")
    void testUserDTO() {
        UserDTO dto = new UserDTO();
        LocalDateTime now = LocalDateTime.now();

        dto.setId(1L);
        dto.setUsername("user1");
        dto.setNickname("昵称");
        dto.setEmail("a@b.com");
        dto.setPhone("13800138000");
        dto.setAvatar("avatar.png");
        dto.setStatus(1);
        dto.setRole("user");
        dto.setCreateTime(now);
        dto.setUpdateTime(now);

        assertEquals(1L, dto.getId());
        assertEquals("user1", dto.getUsername());
        assertEquals("昵称", dto.getNickname());
        assertEquals("a@b.com", dto.getEmail());
        assertEquals("13800138000", dto.getPhone());
        assertEquals("avatar.png", dto.getAvatar());
        assertEquals(1, dto.getStatus());
        assertEquals("user", dto.getRole());
        assertEquals(now, dto.getCreateTime());
        assertEquals(now, dto.getUpdateTime());
    }

    @Test
    @DisplayName("LoginDTO getter/setter")
    void testLoginDTO() {
        LoginDTO dto = new LoginDTO();
        dto.setUsername("admin");
        dto.setPassword("123456");
        dto.setRememberMe(true);

        assertEquals("admin", dto.getUsername());
        assertEquals("123456", dto.getPassword());
        assertTrue(dto.getRememberMe());
    }

    @Test
    @DisplayName("LoginDTO 默认值")
    void testLoginDTODefault() {
        LoginDTO dto = new LoginDTO();
        assertFalse(dto.getRememberMe());
    }
}
