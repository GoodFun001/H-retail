package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.Product;
import com.retail.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductRepository 测试")
class ProductRepositoryTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductRepository productRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setPrice(new BigDecimal("99.00"));
        product.setStock(100);
        product.setStatus(1);
    }

    @Test
    @DisplayName("findById - 查询商品")
    void findById() {
        when(productMapper.selectById(1L)).thenReturn(product);
        Product result = productRepository.findById(1L);
        assertNotNull(result);
        assertEquals("测试商品", result.getName());
    }

    @Test
    @DisplayName("selectById - 别名方法")
    void selectById() {
        when(productMapper.selectById(1L)).thenReturn(product);
        Product result = productRepository.selectById(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("save - 保存商品")
    void save() {
        when(productMapper.insert(product)).thenReturn(1);
        assertTrue(productRepository.save(product));
    }

    @Test
    @DisplayName("insert - 别名保存")
    void insert() {
        when(productMapper.insert(product)).thenReturn(1);
        assertEquals(1, productRepository.insert(product));
    }

    @Test
    @DisplayName("update - 更新商品")
    void update() {
        when(productMapper.updateById(product)).thenReturn(1);
        assertTrue(productRepository.update(product));
    }

    @Test
    @DisplayName("updateById - 别名更新")
    void updateById() {
        when(productMapper.updateById(product)).thenReturn(1);
        assertEquals(1, productRepository.updateById(product));
    }

    @Test
    @DisplayName("deleteById - 删除商品")
    void deleteById() {
        when(productMapper.deleteById(1L)).thenReturn(1);
        assertTrue(productRepository.deleteById(1L));
    }

    @Test
    @DisplayName("findAll - 查询所有")
    void findAll() {
        when(productMapper.selectList(null)).thenReturn(Arrays.asList(product));
        List<Product> list = productRepository.findAll();
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("count - 统计数量")
    void count() {
        when(productMapper.selectCount(null)).thenReturn(10L);
        assertEquals(10L, productRepository.count());
    }

    @Test
    @DisplayName("count with wrapper - 条件统计")
    void countWithWrapper() {
        when(productMapper.selectCount(any())).thenReturn(5L);
        assertEquals(5L, productRepository.count(new LambdaQueryWrapper<>()));
    }

    @Test
    @DisplayName("updateBatchById - 批量更新")
    void updateBatchById() {
        when(productMapper.updateById(any(Product.class))).thenReturn(1);
        assertTrue(productRepository.updateBatchById(Arrays.asList(product, product)));
    }
}
