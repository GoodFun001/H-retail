package com.retail.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.Product;
import com.retail.repository.ProductRepository;
import com.retail.service.impl.ProductServiceImpl;
import com.retail.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService 测试")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserService userService;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setPrice(new BigDecimal("99.00"));
        product.setStock(100);
        product.setStatus(1);
        product.setSalesCount(50);
        product.setImageUrl("img.jpg");
        product.setCategory(1);
        product.setDescription("描述");
        product.setCreateBy(1L);

        ReflectionTestUtils.setField(productService, "hotProductLimit", 10);
        ReflectionTestUtils.setField(productService, "hotProductCacheTime", 30);
        ReflectionTestUtils.setField(productService, "vipDiscountRate", 0.8);
    }

    @Nested
    @DisplayName("商品查询")
    class ProductQuery {

        @Test
        @DisplayName("getProductList - 基本查询")
        void getProductList_Basic() {
            Page<Product> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(product));
            page.setTotal(1);
            when(productRepository.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(product));
            when(productRepository.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            IPage<Product> result = productService.getProductList(1, 10, null, null);
            assertNotNull(result);
            assertEquals(1, result.getTotal());
        }

        @Test
        @DisplayName("getProductList - 带搜索关键词")
        void getProductList_WithSearch() {
            Page<Product> page = new Page<>(1, 10);
            page.setRecords(Arrays.asList(product));
            page.setTotal(1);
            when(productRepository.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(product));
            when(productRepository.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

            IPage<Product> result = productService.getProductList(1, 10, "测试", 1);
            assertNotNull(result);
        }

        @Test
        @DisplayName("getProductList - 异常时返回空Page")
        void getProductList_Exception() {
            when(productRepository.selectList(any())).thenThrow(new RuntimeException("DB error"));
            IPage<Product> result = productService.getProductList(1, 10, null, null);
            assertNotNull(result);
            assertEquals(1, result.getCurrent());
        }

        @Test
        @DisplayName("getProductDetail - 从缓存获取")
        void getProductDetail_FromCache() {
            when(redisUtil.get("product:detail:1")).thenReturn(product);
            Product result = productService.getProductDetail(1L);
            assertNotNull(result);
            assertEquals("测试商品", result.getName());
        }

        @Test
        @DisplayName("getProductDetail - 从数据库获取")
        void getProductDetail_FromDB() {
            when(redisUtil.get("product:detail:1")).thenReturn(null);
            when(productRepository.findById(1L)).thenReturn(product);
            Product result = productService.getProductDetail(1L);
            assertNotNull(result);
            verify(redisUtil).set(eq("product:detail:1"), any(), anyLong(), any());
        }

        @Test
        @DisplayName("getProductDetail - 商品不存在")
        void getProductDetail_NotFound() {
            when(redisUtil.get("product:detail:999")).thenReturn(null);
            when(productRepository.findById(999L)).thenReturn(null);
            Product result = productService.getProductDetail(999L);
            assertNull(result);
        }

        @Test
        @DisplayName("getProductDetail - 异常处理")
        void getProductDetail_Exception() {
            when(redisUtil.get(any())).thenThrow(new RuntimeException("Redis error"));
            Product result = productService.getProductDetail(1L);
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("商品管理")
    class ProductManagement {

        @Test
        @DisplayName("createProduct - 创建成功")
        void createProduct_Success() {
            when(productRepository.save(any(Product.class))).thenReturn(true);
            boolean result = productService.createProduct(product, 1L);
            assertTrue(result);
            assertEquals(1, product.getStatus());
            assertEquals(0, product.getSalesCount());
        }

        @Test
        @DisplayName("createProduct - 异常处理")
        void createProduct_Exception() {
            when(productRepository.save(any())).thenThrow(new RuntimeException("DB error"));
            boolean result = productService.createProduct(product, 1L);
            assertFalse(result);
        }

        @Test
        @DisplayName("updateProduct - 更新成功")
        void updateProduct_Success() {
            when(productRepository.findById(1L)).thenReturn(product);
            when(productRepository.update(any(Product.class))).thenReturn(true);
            boolean result = productService.updateProduct(product, 1L);
            assertTrue(result);
        }

        @Test
        @DisplayName("updateProduct - 商品不存在")
        void updateProduct_NotFound() {
            when(productRepository.findById(1L)).thenReturn(null);
            boolean result = productService.updateProduct(product, 1L);
            assertFalse(result);
        }

        @Test
        @DisplayName("updateProduct - 无权限")
        void updateProduct_NoPermission() {
            when(productRepository.findById(1L)).thenReturn(product);
            boolean result = productService.updateProduct(product, 999L);
            assertFalse(result);
        }

        @Test
        @DisplayName("deleteProduct - 删除成功")
        void deleteProduct_Success() {
            when(productRepository.findById(1L)).thenReturn(product);
            when(productRepository.deleteById(1L)).thenReturn(true);
            boolean result = productService.deleteProduct(1L, 1L);
            assertTrue(result);
        }

        @Test
        @DisplayName("deleteProduct - 无权限")
        void deleteProduct_NoPermission() {
            when(productRepository.findById(1L)).thenReturn(product);
            boolean result = productService.deleteProduct(1L, 999L);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("热点商品")
    class HotProducts {

        @Test
        @DisplayName("getHotProducts - 从缓存获取")
        void getHotProducts_FromCache() {
            String cachedJson = JSON.toJSONString(Arrays.asList(product));
            when(redisUtil.getString(anyString())).thenReturn(cachedJson);
            List<Product> result = productService.getHotProducts();
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("getHotProducts - 从数据库获取")
        void getHotProducts_FromDB() {
            when(redisUtil.getString(anyString())).thenReturn(null);
            when(productRepository.selectList(any(LambdaQueryWrapper.class))).thenReturn(Arrays.asList(product));
            List<Product> result = productService.getHotProducts();
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("getHotProducts - 异常处理")
        void getHotProducts_Exception() {
            when(redisUtil.getString(anyString())).thenThrow(new RuntimeException("Redis error"));
            List<Product> result = productService.getHotProducts();
            assertNull(result);
        }

        @Test
        @DisplayName("refreshHotProductsCache - 刷新缓存")
        void refreshHotProductsCache() {
            productService.refreshHotProductsCache();
            verify(redisUtil).delete(anyString());
        }
    }

    @Nested
    @DisplayName("VIP价格计算")
    class VipPrice {

        @Test
        @DisplayName("calculateVipPrice - VIP用户")
        void calculateVipPrice_Vip() {
            Product vipProduct = productService.calculateVipPrice(product, true);
            assertNotNull(vipProduct);
            assertEquals(0, vipProduct.getPrice().compareTo(new BigDecimal("79.200")));
        }

        @Test
        @DisplayName("calculateVipPrice - 非VIP用户")
        void calculateVipPrice_NotVip() {
            Product result = productService.calculateVipPrice(product, false);
            assertNotNull(result);
            assertEquals(product.getPrice(), result.getPrice());
        }

        @Test
        @DisplayName("calculateVipPrice - 空商品")
        void calculateVipPrice_Null() {
            assertNull(productService.calculateVipPrice(null, true));
        }
    }
}
