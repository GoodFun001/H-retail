package com.retail.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.dto.ProductCreateDTO;
import com.retail.dto.ProductDTO;
import com.retail.dto.ProductUpdateDTO;
import com.retail.entity.Product;
import com.retail.repository.AdminProductRepository;
import com.retail.service.impl.AdminProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminProductService 测试")
class AdminProductServiceTest {

    @Mock
    private AdminProductRepository adminProductRepository;

    @InjectMocks
    private AdminProductServiceImpl adminProductService;

    private Product product;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setDescription("描述");
        product.setPrice(new BigDecimal("99.00"));
        product.setStock(100);
        product.setStatus(1);
        product.setSalesCount(50);
        product.setCategory(1);
        product.setImageUrl("img.jpg");
        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("商品CRUD")
    class ProductCRUD {

        @Test
        @DisplayName("getAllProducts - 分页查询")
        void getAllProducts() {
            Page<Product> mpPage = new Page<>(1, 10);
            mpPage.setRecords(Arrays.asList(product));
            mpPage.setTotal(1);
            when(adminProductRepository.selectPage(any(Page.class), isNull())).thenReturn(mpPage);

            org.springframework.data.domain.Page<ProductDTO> result = adminProductService.getAllProducts(pageable);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("getProductById - 存在")
        void getProductById_Exists() {
            when(adminProductRepository.selectById(1L)).thenReturn(product);
            ProductDTO result = adminProductService.getProductById(1L);
            assertNotNull(result);
            assertEquals("测试商品", result.getName());
        }

        @Test
        @DisplayName("getProductById - 不存在")
        void getProductById_NotFound() {
            when(adminProductRepository.selectById(999L)).thenReturn(null);
            assertNull(adminProductService.getProductById(999L));
        }

        @Test
        @DisplayName("createProduct - 创建成功")
        void createProduct_Success() {
            ProductCreateDTO dto = new ProductCreateDTO();
            dto.setName("新商品");
            dto.setPrice(new BigDecimal("50.00"));
            dto.setStock(200);

            when(adminProductRepository.selectCount(any())).thenReturn(0L);
            when(adminProductRepository.insert(any())).thenReturn(1);

            ProductDTO result = adminProductService.createProduct(dto);
            assertNotNull(result);
        }

        @Test
        @DisplayName("createProduct - 名称重复")
        void createProduct_DuplicateName() {
            ProductCreateDTO dto = new ProductCreateDTO();
            dto.setName("测试商品");

            when(adminProductRepository.selectCount(any())).thenReturn(1L);

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> adminProductService.createProduct(dto));
            assertTrue(ex.getMessage().contains("产品名称已存在"));
        }

        @Test
        @DisplayName("updateProduct - 更新成功")
        void updateProduct_Success() {
            ProductUpdateDTO dto = new ProductUpdateDTO();
            dto.setName("更新商品");
            dto.setPrice(new BigDecimal("199.00"));

            when(adminProductRepository.selectById(1L)).thenReturn(product);
            when(adminProductRepository.selectCount(any())).thenReturn(0L);
            when(adminProductRepository.updateById(any())).thenReturn(1);

            ProductDTO result = adminProductService.updateProduct(1L, dto);
            assertNotNull(result);
        }

        @Test
        @DisplayName("updateProduct - 不存在")
        void updateProduct_NotFound() {
            when(adminProductRepository.selectById(999L)).thenReturn(null);
            assertThrows(RuntimeException.class,
                    () -> adminProductService.updateProduct(999L, new ProductUpdateDTO()));
        }

        @Test
        @DisplayName("deleteProduct - 删除成功")
        void deleteProduct_Success() {
            when(adminProductRepository.selectById(1L)).thenReturn(product);
            when(adminProductRepository.deleteById(1L)).thenReturn(1);
            assertTrue(adminProductService.deleteProduct(1L));
        }

        @Test
        @DisplayName("deleteProduct - 不存在")
        void deleteProduct_NotFound() {
            when(adminProductRepository.selectById(999L)).thenReturn(null);
            assertFalse(adminProductService.deleteProduct(999L));
        }

        @Test
        @DisplayName("batchDeleteProducts - 批量删除")
        void batchDeleteProducts() {
            when(adminProductRepository.deleteBatchIds(anyList())).thenReturn(2);
            assertEquals(2, adminProductService.batchDeleteProducts(Arrays.asList(1L, 2L)));
        }

        @Test
        @DisplayName("batchDeleteProducts - 空列表")
        void batchDeleteProducts_Empty() {
            assertEquals(0, adminProductService.batchDeleteProducts(Collections.emptyList()));
        }
    }

    @Nested
    @DisplayName("商品状态管理")
    class ProductStatus {

        @Test
        @DisplayName("updateProductStatus - 成功")
        @org.junit.jupiter.api.Disabled("需要MyBatis-Plus Lambda缓存初始化，使用@SpringBootTest集成测试验证")
        void updateProductStatus_Success() {
            when(adminProductRepository.selectById(1L)).thenReturn(product);
            when(adminProductRepository.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);
            assertTrue(adminProductService.updateProductStatus(1L, 0));
        }

        @Test
        @DisplayName("updateProductStatus - 不存在")
        void updateProductStatus_NotFound() {
            when(adminProductRepository.selectById(999L)).thenReturn(null);
            assertFalse(adminProductService.updateProductStatus(999L, 0));
        }

        @Test
        @DisplayName("batchUpdateProductStatus")
        @org.junit.jupiter.api.Disabled("需要MyBatis-Plus Lambda缓存初始化，使用@SpringBootTest集成测试验证")
        void batchUpdateProductStatus() {
            when(adminProductRepository.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(2);
            assertEquals(2, adminProductService.batchUpdateProductStatus(Arrays.asList(1L, 2L), 0));
        }

        @Test
        @DisplayName("batchUpdateProductStatus - 空列表")
        void batchUpdateProductStatus_Empty() {
            assertEquals(0, adminProductService.batchUpdateProductStatus(Collections.emptyList(), 0));
        }
    }

    @Nested
    @DisplayName("商品搜索")
    class ProductSearch {

        @Test
        @DisplayName("searchProducts - 关键词搜索")
        void searchProducts() {
            Page<Product> mpPage = new Page<>(1, 10);
            mpPage.setRecords(Arrays.asList(product));
            mpPage.setTotal(1);
            when(adminProductRepository.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
                    .thenReturn(mpPage);

            org.springframework.data.domain.Page<ProductDTO> result =
                    adminProductService.searchProducts("测试", null, null, null, null, pageable);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("商品统计")
    class ProductStatistics {

        @Test
        @DisplayName("getProductStatistics")
        void getProductStatistics() {
            when(adminProductRepository.selectCount(any(LambdaQueryWrapper.class))).thenReturn(100L, 60L, 40L, 5L, 3L, 15L, 50L);
            Object stats = adminProductService.getProductStatistics();
            assertNotNull(stats);
            assertTrue(stats instanceof Map);
        }
    }

    @Nested
    @DisplayName("库存管理")
    class StockManagement {

        @Test
        @DisplayName("getLowStockProducts")
        void getLowStockProducts() {
            when(adminProductRepository.selectList(any(LambdaQueryWrapper.class)))
                    .thenReturn(Arrays.asList(product));
            List<ProductDTO> result = adminProductService.getLowStockProducts(null);
            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("updateProductStock")
        @org.junit.jupiter.api.Disabled("需要MyBatis-Plus Lambda缓存初始化，使用@SpringBootTest集成测试验证")
        void updateProductStock() {
            when(adminProductRepository.selectById(1L)).thenReturn(product);
            when(adminProductRepository.update(isNull(), any(LambdaUpdateWrapper.class))).thenReturn(1);
            assertTrue(adminProductService.updateProductStock(1L, 200));
        }

        @Test
        @DisplayName("updateProductStock - 不存在")
        void updateProductStock_NotFound() {
            when(adminProductRepository.selectById(999L)).thenReturn(null);
            assertFalse(adminProductService.updateProductStock(999L, 200));
        }
    }

    @Nested
    @DisplayName("分类管理")
    class CategoryManagement {

        @Test
        @DisplayName("getProductCategories")
        void getProductCategories() {
            List<Map<String, Object>> categories = adminProductService.getProductCategories();
            assertNotNull(categories);
            assertEquals(5, categories.size());
            assertEquals("电子产品", categories.get(0).get("name"));
        }
    }

    @Nested
    @DisplayName("复制商品")
    class CopyProduct {

        @Test
        @DisplayName("copyProduct - 成功")
        void copyProduct_Success() {
            when(adminProductRepository.selectById(1L)).thenReturn(product);
            when(adminProductRepository.selectCount(any())).thenReturn(0L);
            when(adminProductRepository.insert(any())).thenReturn(1);

            ProductDTO result = adminProductService.copyProduct(1L, "复制商品");
            assertNotNull(result);
        }
    }
}
