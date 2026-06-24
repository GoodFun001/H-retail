package com.retail.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.common.Result;
import com.retail.entity.Product;
import com.retail.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminProductController 测试")
class AdminProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private AdminProductController controller;

    private Product product;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setDescription("描述");
        product.setPrice(new BigDecimal("99.00"));
        product.setStock(100);
        product.setStatus(1);
        product.setCategory(1);

        request = new MockHttpServletRequest();
    }

    @Test
    @DisplayName("getProductList - 正常分页")
    void getProductList_Success() {
        Page<Product> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(product));
        page.setTotal(1);
        when(productService.page(any(Page.class))).thenReturn(page);

        Result<IPage<Product>> result = controller.getProductList(1, 10);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("getProductList - 异常")
    void getProductList_Exception() {
        when(productService.page(any(Page.class))).thenThrow(new RuntimeException("DB error"));

        Result<IPage<Product>> result = controller.getProductList(1, 10);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("获取商品列表失败"));
    }

    @Test
    @DisplayName("searchProducts - 成功")
    void searchProducts_Success() {
        Page<Product> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(product));
        when(productService.getProductList(eq(1), eq(10), eq("测试"), isNull())).thenReturn(page);

        Result<IPage<Product>> result = controller.searchProducts("测试", 1, 10);
        assertEquals(200, result.getCode());
    }

    @Test
    @DisplayName("searchProducts - 异常")
    void searchProducts_Exception() {
        when(productService.getProductList(anyInt(), anyInt(), anyString(), isNull()))
                .thenThrow(new RuntimeException("error"));

        Result<IPage<Product>> result = controller.searchProducts("测试", 1, 10);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("搜索商品失败"));
    }

    @Test
    @DisplayName("createProduct - 成功")
    void createProduct_Success() {
        when(productService.createProduct(any(Product.class), eq(1L))).thenReturn(true);

        Result<String> result = controller.createProduct(product, request);
        assertEquals(200, result.getCode());
        assertEquals("创建商品成功", result.getData());
    }

    @Test
    @DisplayName("createProduct - 失败")
    void createProduct_Failed() {
        when(productService.createProduct(any(Product.class), eq(1L))).thenReturn(false);

        Result<String> result = controller.createProduct(product, request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("创建商品失败"));
    }

    @Test
    @DisplayName("createProduct - 异常")
    void createProduct_Exception() {
        when(productService.createProduct(any(Product.class), eq(1L)))
                .thenThrow(new RuntimeException("error"));

        Result<String> result = controller.createProduct(product, request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("创建商品失败"));
    }

    @Test
    @DisplayName("getProductDetail - 存在")
    void getProductDetail_Exists() {
        when(productService.getProductDetail(1L)).thenReturn(product);

        Result<Product> result = controller.getProductDetail(1L);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("getProductDetail - 不存在")
    void getProductDetail_NotFound() {
        when(productService.getProductDetail(999L)).thenReturn(null);

        Result<Product> result = controller.getProductDetail(999L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("商品不存在"));
    }

    @Test
    @DisplayName("getProductDetail - 异常")
    void getProductDetail_Exception() {
        when(productService.getProductDetail(1L)).thenThrow(new RuntimeException("error"));

        Result<Product> result = controller.getProductDetail(1L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("获取商品详情失败"));
    }

    @Test
    @DisplayName("updateProduct - 成功")
    void updateProduct_Success() {
        when(productService.updateProduct(any(Product.class), eq(1L))).thenReturn(true);

        Result<String> result = controller.updateProduct(1L, product, request);
        assertEquals(200, result.getCode());
        assertEquals("更新商品成功", result.getData());
    }

    @Test
    @DisplayName("updateProduct - 失败")
    void updateProduct_Failed() {
        when(productService.updateProduct(any(Product.class), eq(1L))).thenReturn(false);

        Result<String> result = controller.updateProduct(1L, product, request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新商品失败"));
    }

    @Test
    @DisplayName("updateProduct - 异常")
    void updateProduct_Exception() {
        when(productService.updateProduct(any(Product.class), eq(1L)))
                .thenThrow(new RuntimeException("error"));

        Result<String> result = controller.updateProduct(1L, product, request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新商品失败"));
    }

    @Test
    @DisplayName("deleteProduct - 成功")
    void deleteProduct_Success() {
        when(productService.deleteProduct(1L, 1L)).thenReturn(true);

        Result<String> result = controller.deleteProduct(1L, request);
        assertEquals(200, result.getCode());
        assertEquals("删除商品成功", result.getData());
    }

    @Test
    @DisplayName("deleteProduct - 失败")
    void deleteProduct_Failed() {
        when(productService.deleteProduct(1L, 1L)).thenReturn(false);

        Result<String> result = controller.deleteProduct(1L, request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("删除商品失败"));
    }

    @Test
    @DisplayName("deleteProduct - 异常")
    void deleteProduct_Exception() {
        when(productService.deleteProduct(1L, 1L)).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.deleteProduct(1L, request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("删除商品失败"));
    }

    @Test
    @DisplayName("updateProductStock - 成功")
    void updateProductStock_Success() {
        when(productService.getById(1L)).thenReturn(product);
        when(productService.updateById(any(Product.class))).thenReturn(true);

        Result<String> result = controller.updateProductStock(1L, 200);
        assertEquals(200, result.getCode());
        assertEquals("更新商品库存成功", result.getData());
    }

    @Test
    @DisplayName("updateProductStock - 商品不存在")
    void updateProductStock_NotFound() {
        when(productService.getById(999L)).thenReturn(null);

        Result<String> result = controller.updateProductStock(999L, 200);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("商品不存在"));
    }

    @Test
    @DisplayName("updateProductStock - 异常")
    void updateProductStock_Exception() {
        when(productService.getById(1L)).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.updateProductStock(1L, 200);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("失败"));
    }

    @Test
    @DisplayName("updateProductStatus - 成功")
    void updateProductStatus_Success() {
        when(productService.getById(1L)).thenReturn(product);
        when(productService.updateById(any(Product.class))).thenReturn(true);

        Result<String> result = controller.updateProductStatus(1L, 0);
        assertEquals(200, result.getCode());
        assertEquals("更新商品状态成功", result.getData());
    }

    @Test
    @DisplayName("updateProductStatus - 商品不存在")
    void updateProductStatus_NotFound() {
        when(productService.getById(999L)).thenReturn(null);

        Result<String> result = controller.updateProductStatus(999L, 0);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("商品不存在"));
    }

    @Test
    @DisplayName("updateProductStatus - 异常")
    void updateProductStatus_Exception() {
        when(productService.getById(1L)).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.updateProductStatus(1L, 0);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("失败"));
    }

    @Test
    @DisplayName("batchUpdateProductStock - 成功")
    void batchUpdateProductStock_Success() {
        when(productService.updateBatchById(anyList())).thenReturn(true);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("stock", 200);
        list.add(item);

        Result<String> result = controller.batchUpdateProductStock(list);
        assertEquals(200, result.getCode());
        assertEquals("批量更新商品库存成功", result.getData());
    }

    @Test
    @DisplayName("batchUpdateProductStock - 异常")
    void batchUpdateProductStock_Exception() {
        when(productService.updateBatchById(anyList())).thenThrow(new RuntimeException("error"));

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("stock", 200);
        list.add(item);

        Result<String> result = controller.batchUpdateProductStock(list);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("批量更新商品库存失败"));
    }

    @Test
    @DisplayName("batchUpdateProductStatus - 成功")
    void batchUpdateProductStatus_Success() {
        when(productService.updateBatchById(anyList())).thenReturn(true);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("status", 0);
        list.add(item);

        Result<String> result = controller.batchUpdateProductStatus(list);
        assertEquals(200, result.getCode());
        assertEquals("批量更新商品状态成功", result.getData());
    }

    @Test
    @DisplayName("batchUpdateProductStatus - 异常")
    void batchUpdateProductStatus_Exception() {
        when(productService.updateBatchById(anyList())).thenThrow(new RuntimeException("error"));

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("status", 0);
        list.add(item);

        Result<String> result = controller.batchUpdateProductStatus(list);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("批量更新商品状态失败"));
    }
}
