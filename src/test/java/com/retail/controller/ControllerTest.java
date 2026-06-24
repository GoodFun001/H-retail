package com.retail.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.common.Result;
import com.retail.entity.Product;
import com.retail.entity.User;
import com.retail.service.ProductService;
import com.retail.service.UserService;
import com.retail.util.JwtUtil;
import com.retail.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Controller 层测试")
class ControllerTest {

    // ==================== HealthController ====================

    @Nested
    @DisplayName("HealthController")
    class HealthControllerTests {

        @Test
        @DisplayName("health - 返回UP状态")
        void health() {
            HealthController controller = new HealthController();
            Result<Map<String, Object>> result = controller.health();
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("UP", result.getData().get("status"));
            assertEquals("retail-platform", result.getData().get("service"));
        }
    }

    // ==================== ProductController ====================

    @Nested
    @DisplayName("ProductController")
    class ProductControllerTests {

        @Mock
        private ProductService productService;

        @InjectMocks
        private ProductController productController;

        private Product product;

        @BeforeEach
        void setUp() {
            product = new Product();
            product.setId(1L);
            product.setName("测试商品");
            product.setPrice(new BigDecimal("99.00"));
            product.setStock(100);
            product.setStatus(1);
            product.setCategory(1);
        }

        @Test
        @DisplayName("getProductList - 成功")
        void getProductList_Success() {
            Page<Product> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(product));
            page.setTotal(1);
            when(productService.getProductList(1, 10, null, null)).thenReturn(page);

            Result<IPage<Product>> result = productController.getProductList(1, 10, null, null);
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
        }

        @Test
        @DisplayName("getProductList - 异常")
        void getProductList_Exception() {
            when(productService.getProductList(anyInt(), anyInt(), any(), any()))
                    .thenThrow(new RuntimeException("DB error"));
            Result<IPage<Product>> result = productController.getProductList(1, 10, null, null);
            assertEquals(500, result.getCode());
            assertEquals("获取商品列表失败", result.getMsg());
        }

        @Test
        @DisplayName("searchProducts - 成功")
        void searchProducts_Success() {
            Page<Product> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(product));
            when(productService.getProductList(1, 10, "test", null)).thenReturn(page);

            Result<IPage<Product>> result = productController.searchProducts("test", 1, 10);
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("getProductsByCategory - 成功")
        void getProductsByCategory_Success() {
            Page<Product> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(product));
            when(productService.getProductList(1, 10, null, 1)).thenReturn(page);

            Result<IPage<Product>> result = productController.getProductsByCategory(1, 1, 10);
            assertEquals(200, result.getCode());
        }

        @Test
        @DisplayName("getProductById - 商品存在")
        void getProductById_Exists() {
            when(productService.getProductDetail(1L)).thenReturn(product);
            Result<Product> result = productController.getProductById(1L);
            assertEquals(200, result.getCode());
            assertEquals("测试商品", result.getData().getName());
        }

        @Test
        @DisplayName("getProductById - 商品不存在")
        void getProductById_NotFound() {
            when(productService.getProductDetail(999L)).thenReturn(null);
            Result<Product> result = productController.getProductById(999L);
            assertEquals(500, result.getCode());
            assertEquals("商品不存在", result.getMsg());
        }
    }

    // ==================== UserController ====================

    @Nested
    @DisplayName("UserController")
    class UserControllerTests {

        @Mock
        private UserService userService;

        @Mock
        private JwtUtil jwtUtil;

        @Mock
        private RedisUtil redisUtil;

        @InjectMocks
        private UserController userController;

        @Test
        @DisplayName("register - 成功")
        void register_Success() {
            when(userService.register(any(User.class))).thenReturn(true);
            User user = new User();
            user.setUsername("test");
            user.setPassword("pass");
            Result<String> result = userController.register(user);
            assertEquals(200, result.getCode());
            assertEquals("注册成功", result.getData());
        }

        @Test
        @DisplayName("register - 用户名已存在")
        void register_Failed() {
            when(userService.register(any(User.class))).thenReturn(false);
            User user = new User();
            user.setUsername("test");
            Result<String> result = userController.register(user);
            assertEquals(400, result.getCode());
        }

        @Test
        @DisplayName("register - 异常")
        void register_Exception() {
            when(userService.register(any())).thenThrow(new RuntimeException("DB error"));
            Result<String> result = userController.register(new User());
            assertEquals(500, result.getCode());
        }

        @Test
        @DisplayName("logout - 成功")
        void logout_Success() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addHeader("Authorization", "Bearer token123");
            Result<String> result = userController.logout(request);
            assertEquals(200, result.getCode());
            verify(redisUtil).delete("user:session:token123");
        }

        @Test
        @DisplayName("logout - 无Token")
        void logout_NoToken() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            Result<String> result = userController.logout(request);
            assertEquals(200, result.getCode());
            assertEquals("退出登录成功", result.getData());
        }
    }
}
