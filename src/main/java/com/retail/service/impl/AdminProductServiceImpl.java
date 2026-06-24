package com.retail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import com.retail.dto.ProductCreateDTO;
import com.retail.dto.ProductDTO;
import com.retail.dto.ProductUpdateDTO;
import com.retail.entity.Product;
import com.retail.repository.AdminProductRepository;
import com.retail.service.AdminProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员产品服务实现类
 * 【重构说明】引入Repository层，Service通过AdminProductRepository访问数据，
 * 不再直接依赖AdminProductMapper，符合分层架构原则。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final AdminProductRepository adminProductRepository;

    /** 低库存阈值 - 可配置化（原硬编码为10） */
    private static final int LOW_STOCK_THRESHOLD = 10;

    @Override
    public org.springframework.data.domain.Page<ProductDTO> getAllProducts(Pageable pageable) {
        log.info("获取所有产品列表，页码: {}, 每页大小: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        // 转换为MyBatis-Plus的Page对象
        Page<Product> page = 
            new Page<>(
                pageable.getPageNumber() + 1, // MyBatis-Plus页码从1开始
                pageable.getPageSize()
            );
        
        Page<Product> productPage = adminProductRepository.selectPage(page, null);
        
        // 转换为DTO
        List<ProductDTO> productDTOs = productPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        // 转换为Spring Data的Page对象
        org.springframework.data.domain.Page<ProductDTO> result = new PageImpl<>(productDTOs, pageable, productPage.getTotal());
        
        log.info("获取产品列表成功，总数: {}", productPage.getTotal());
        return result;
    }

    @Override
    public ProductDTO getProductById(Long id) {
        log.info("根据ID获取产品: {}", id);
        
        Product product = adminProductRepository.selectById(id);
        if (product == null) {
            log.warn("产品不存在: {}", id);
            return null;
        }
        
        ProductDTO productDTO = convertToDTO(product);
        log.info("获取产品成功: {}", product.getName());
        return productDTO;
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductCreateDTO productCreateDTO) {
        log.info("创建产品: {}", productCreateDTO.getName());
        
        // 检查产品名称是否已存在
        if (existsByName(productCreateDTO.getName(), null)) {
            throw new RuntimeException("产品名称已存在: " + productCreateDTO.getName());
        }
        
        // 检查产品编码是否已存在
        if (StringUtils.hasText(productCreateDTO.getCode()) && 
            existsByCode(productCreateDTO.getCode(), null)) {
            throw new RuntimeException("产品编码已存在: " + productCreateDTO.getCode());
        }
        
        Product product = new Product();
        BeanUtils.copyProperties(productCreateDTO, product);
        product.setStatus(1); // 默认状态为上架
        
        // 设置默认值
        if (product.getSales() == null) {
            product.setSales(0);
        }
        
        adminProductRepository.insert(product);
        
        ProductDTO productDTO = convertToDTO(product);
        log.info("创建产品成功: {}", product.getName());
        return productDTO;
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO) {
        log.info("更新产品: {}", id);
        
        Product existingProduct = adminProductRepository.selectById(id);
        if (existingProduct == null) {
            throw new RuntimeException("产品不存在: " + id);
        }
        
        // 检查产品名称是否已存在（排除当前产品）
        if (StringUtils.hasText(productUpdateDTO.getName()) && 
            existsByName(productUpdateDTO.getName(), id)) {
            throw new RuntimeException("产品名称已存在: " + productUpdateDTO.getName());
        }
        
        // 检查产品编码是否已存在（排除当前产品）
        if (StringUtils.hasText(productUpdateDTO.getCode()) && 
            existsByCode(productUpdateDTO.getCode(), id)) {
            throw new RuntimeException("产品编码已存在: " + productUpdateDTO.getCode());
        }
        
        BeanUtils.copyProperties(productUpdateDTO, existingProduct, "id", "createTime");
        
        adminProductRepository.updateById(existingProduct);
        
        ProductDTO productDTO = convertToDTO(existingProduct);
        log.info("更新产品成功: {}", existingProduct.getName());
        return productDTO;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        log.info("删除产品: {}", id);
        
        Product product = adminProductRepository.selectById(id);
        if (product == null) {
            log.warn("删除产品失败，产品不存在: {}", id);
            return false;
        }
        
        int result = adminProductRepository.deleteById(id);
        boolean success = result > 0;
        
        if (success) {
            log.info("删除产品成功: {}", product.getName());
        } else {
            log.warn("删除产品失败: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional
    public int batchDeleteProducts(List<Long> ids) {
        log.info("批量删除产品，数量: {}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int result = adminProductRepository.deleteBatchIds(ids);
        log.info("批量删除产品完成，成功删除数量: {}", result);
        return result;
    }

    @Override
    @Transactional
    public boolean updateProductStatus(Long id, Integer status) {
        log.info("更新产品状态: {} -> {}", id, status);
        
        Product product = adminProductRepository.selectById(id);
        if (product == null) {
            log.warn("更新产品状态失败，产品不存在: {}", id);
            return false;
        }
        
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                    .set(Product::getStatus, status);
        
        int result = adminProductRepository.update(null, updateWrapper);
        boolean success = result > 0;
        
        if (success) {
            log.info("更新产品状态成功: {} -> {}", id, status);
        } else {
            log.warn("更新产品状态失败: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional
    public int batchUpdateProductStatus(List<Long> ids, Integer status) {
        log.info("批量更新产品状态，数量: {}, 状态: {}", ids.size(), status);
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Product::getId, ids)
                    .set(Product::getStatus, status);
        
        int result = adminProductRepository.update(null, updateWrapper);
        log.info("批量更新产品状态完成，成功更新数量: {}", result);
        return result;
    }

    @Override
    public org.springframework.data.domain.Page<ProductDTO> searchProducts(String keyword, String category, Integer status, 
                                          Double minPrice, Double maxPrice, Pageable pageable) {
        log.info("搜索产品，关键词: {}, 分类: {}, 状态: {}, 价格范围: {} - {}", 
                keyword, category, status, minPrice, maxPrice);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        
        // 关键词搜索
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(Product::getName, keyword)
                .or()
                .like(Product::getDescription, keyword)
            );
        }
        
        // 分类过滤
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(Product::getCategory, Integer.valueOf(category));
        }
        
        // 状态过滤
        if (status != null) {
            queryWrapper.eq(Product::getStatus, status);
        }
        
        // 价格范围过滤
        if (minPrice != null) {
            queryWrapper.ge(Product::getPrice, minPrice);
        }
        if (maxPrice != null) {
            queryWrapper.le(Product::getPrice, maxPrice);
        }
        
        // 按创建时间降序排序
        queryWrapper.orderByDesc(Product::getCreateTime);
        
        // 分页查询
        Page<Product> page = 
            new Page<>(
                pageable.getPageNumber() + 1,
                pageable.getPageSize()
            );
        
        Page<Product> productPage = adminProductRepository.selectPage(page, queryWrapper);
        
        // 转换为DTO
        List<ProductDTO> productDTOs = productPage.getRecords().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        // 转换为Spring Data的Page对象
        org.springframework.data.domain.Page<ProductDTO> result = new PageImpl<>(productDTOs, pageable, productPage.getTotal());
        
        log.info("搜索产品完成，总数: {}", productPage.getTotal());
        return result;
    }

    @Override
    public Object getProductStatistics() {
        log.info("获取产品统计信息");
        
        Map<String, Object> statistics = new HashMap<>();
        
        // 总产品数
        LambdaQueryWrapper<Product> totalWrapper = new LambdaQueryWrapper<>();
        Long totalCount = adminProductRepository.selectCount(totalWrapper);
        statistics.put("totalProducts", totalCount);
        
        // 上架产品数
        LambdaQueryWrapper<Product> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.eq(Product::getStatus, 1);
        Long activeCount = adminProductRepository.selectCount(activeWrapper);
        statistics.put("activeProducts", activeCount);
        
        // 下架产品数
        LambdaQueryWrapper<Product> inactiveWrapper = new LambdaQueryWrapper<>();
        inactiveWrapper.eq(Product::getStatus, 0);
        Long inactiveCount = adminProductRepository.selectCount(inactiveWrapper);
        statistics.put("inactiveProducts", inactiveCount);
        
        // 库存不足产品数
        LambdaQueryWrapper<Product> lowStockWrapper = new LambdaQueryWrapper<>();
        lowStockWrapper.le(Product::getStock, LOW_STOCK_THRESHOLD);
        Long lowStockCount = adminProductRepository.selectCount(lowStockWrapper);
        statistics.put("lowStockProducts", lowStockCount);
        
        // 今日新增产品数
        statistics.put("todayNewProducts", getTodayNewProductCount());
        
        // 本周新增产品数
        statistics.put("weekNewProducts", getWeekNewProductCount());
        
        // 本月新增产品数
        statistics.put("monthNewProducts", getMonthNewProductCount());
        
        log.info("获取产品统计信息完成");
        return statistics;
    }

    @Override
    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        log.info("获取库存不足产品，阈值: {}", threshold);
        
        if (threshold == null) {
            threshold = LOW_STOCK_THRESHOLD;
        }
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(Product::getStock, threshold)
                   .eq(Product::getStatus, 1) // 只查询上架产品
                   .orderByAsc(Product::getStock);
        
        List<Product> products = adminProductRepository.selectList(queryWrapper);
        List<ProductDTO> productDTOs = products.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        log.info("获取库存不足产品完成，数量: {}", productDTOs.size());
        return productDTOs;
    }

    @Override
    public List<ProductDTO> getTopSellingProducts(Integer limit) {
        log.info("获取热销产品，限制: {}", limit);
        
        if (limit == null) {
            limit = 10; // 默认限制
        }
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1) // 只查询上架产品
                   .orderByDesc(Product::getSales)
                   .last("LIMIT " + limit);
        
        List<Product> products = adminProductRepository.selectList(queryWrapper);
        List<ProductDTO> productDTOs = products.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        log.info("获取热销产品完成，数量: {}", productDTOs.size());
        return productDTOs;
    }

    @Override
    public List<Map<String, Object>> getProductCategories() {
        log.info("获取产品分类列表");
        
        // 创建所有可能的分类，而不仅仅是已使用的
        List<Map<String, Object>> categories = new ArrayList<>();
        
        Map<String, Object> category1 = new HashMap<>();
        category1.put("id", 1);
        category1.put("name", "电子产品");
        categories.add(category1);
        
        Map<String, Object> category2 = new HashMap<>();
        category2.put("id", 2);
        category2.put("name", "服装");
        categories.add(category2);
        
        Map<String, Object> category3 = new HashMap<>();
        category3.put("id", 3);
        category3.put("name", "食品");
        categories.add(category3);
        
        Map<String, Object> category4 = new HashMap<>();
        category4.put("id", 4);
        category4.put("name", "图书");
        categories.add(category4);
        
        Map<String, Object> category5 = new HashMap<>();
        category5.put("id", 5);
        category5.put("name", "其他");
        categories.add(category5);
        
        log.info("获取产品分类列表完成，数量: {}", categories.size());
        return categories;
    }

    @Override
    @Transactional
    public boolean updateProductStock(Long id, Integer stock) {
        log.info("更新产品库存: {} -> {}", id, stock);
        
        Product product = adminProductRepository.selectById(id);
        if (product == null) {
            log.warn("更新产品库存失败，产品不存在: {}", id);
            return false;
        }
        
        LambdaUpdateWrapper<Product> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Product::getId, id)
                    .set(Product::getStock, stock);
        
        int result = adminProductRepository.update(null, updateWrapper);
        boolean success = result > 0;
        
        if (success) {
            log.info("更新产品库存成功: {} -> {}", id, stock);
        } else {
            log.warn("更新产品库存失败: {}", id);
        }
        
        return success;
    }

    @Override
    @Transactional
    public int batchUpdateProductStock(List<Map<String, Object>> stockUpdates) {
        log.info("批量更新产品库存，数量: {}", stockUpdates.size());
        
        if (stockUpdates == null || stockUpdates.isEmpty()) {
            return 0;
        }
        
        int successCount = 0;
        for (Map<String, Object> update : stockUpdates) {
            Long id = Long.valueOf(update.get("id").toString());
            Integer stock = Integer.valueOf(update.get("stock").toString());
            
            if (updateProductStock(id, stock)) {
                successCount++;
            }
        }
        
        log.info("批量更新产品库存完成，成功更新数量: {}", successCount);
        return successCount;
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getName, name);
        if (excludeId != null) {
            queryWrapper.ne(Product::getId, excludeId);
        }
        
        Long count = adminProductRepository.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public boolean existsByCode(String code, Long excludeId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getCode, code);
        if (excludeId != null) {
            queryWrapper.ne(Product::getId, excludeId);
        }
        
        Long count = adminProductRepository.selectCount(queryWrapper);
        return count > 0;
    }

    @Override
    public long getTodayNewProductCount() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Product::getCreateTime, todayStart, todayEnd);
        
        return adminProductRepository.selectCount(queryWrapper);
    }

    @Override
    public long getWeekNewProductCount() {
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime weekEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Product::getCreateTime, weekStart, weekEnd);
        
        return adminProductRepository.selectCount(queryWrapper);
    }

    @Override
    public long getMonthNewProductCount() {
        LocalDateTime monthStart = LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime monthEnd = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(Product::getCreateTime, monthStart, monthEnd);
        
        return adminProductRepository.selectCount(queryWrapper);
    }

    @Override
    public List<ProductDTO> exportProducts() {
        log.info("导出产品数据");
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Product::getCreateTime);
        
        List<Product> products = adminProductRepository.selectList(queryWrapper);
        List<ProductDTO> productDTOs = products.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
        
        log.info("导出产品数据完成，数量: {}", productDTOs.size());
        return productDTOs;
    }

    @Override
    @Transactional
    public ProductDTO copyProduct(Long id, String newName) {
        log.info("复制产品: {} -> {}", id, newName);
        
        Product originalProduct = adminProductRepository.selectById(id);
        if (originalProduct == null) {
            throw new RuntimeException("原产品不存在: " + id);
        }
        
        // 检查新产品名称是否已存在
        if (existsByName(newName, null)) {
            throw new RuntimeException("产品名称已存在: " + newName);
        }
        
        Product newProduct = new Product();
        BeanUtils.copyProperties(originalProduct, newProduct, "id", "createTime", "salesCount");
        
        newProduct.setName(newName);
        newProduct.setStatus(0); // 复制的产品默认为下架状态
        newProduct.setSales(0);
        
        adminProductRepository.insert(newProduct);
        
        ProductDTO productDTO = convertToDTO(newProduct);
        log.info("复制产品成功: {}", newProduct.getName());
        return productDTO;
    }

    @Override
    public List<Map<String, Object>> quickSearchProducts(String keyword, Integer limit) {
        log.info("快速搜索产品，关键词: {}, 限制: {}", keyword, limit);
        
        if (limit == null) {
            limit = 10; // 默认限制
        }
        
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Product::getStatus, 1) // 只查询上架产品
                   .and(StringUtils.hasText(keyword), w -> w
                       .like(Product::getName, keyword)
                   )
                   .orderByDesc(Product::getSales)
                   .last("LIMIT " + limit);
        
        List<Product> products = adminProductRepository.selectList(queryWrapper);
        
        // 转换为简化的Map格式
        List<Map<String, Object>> result = products.stream()
            .map(product -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", product.getId());
                map.put("name", product.getName());
                map.put("price", product.getPrice());
                map.put("stock", product.getStock());
                return map;
            })
            .collect(Collectors.toList());
        
        log.info("快速搜索产品完成，数量: {}", result.size());
        return result;
    }

    /**
     * 将Product实体转换为ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }
}
