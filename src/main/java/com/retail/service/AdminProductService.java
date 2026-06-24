package com.retail.service;

import com.retail.dto.ProductCreateDTO;
import com.retail.dto.ProductDTO;
import com.retail.dto.ProductUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 管理员产品服务接口
 */
public interface AdminProductService {

    /**
     * 获取所有产品（分页）
     */
    Page<ProductDTO> getAllProducts(Pageable pageable);

    /**
     * 根据ID获取产品
     */
    ProductDTO getProductById(Long id);

    /**
     * 创建产品
     */
    ProductDTO createProduct(ProductCreateDTO productCreateDTO);

    /**
     * 更新产品
     */
    ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO);

    /**
     * 删除产品
     */
    boolean deleteProduct(Long id);

    /**
     * 批量删除产品
     */
    int batchDeleteProducts(List<Long> ids);

    /**
     * 更新产品状态
     */
    boolean updateProductStatus(Long id, Integer status);

    /**
     * 批量更新产品状态
     */
    int batchUpdateProductStatus(List<Long> ids, Integer status);

    /**
     * 搜索产品
     */
    Page<ProductDTO> searchProducts(String keyword, String category, Integer status, 
                                   Double minPrice, Double maxPrice, Pageable pageable);

    /**
     * 获取产品统计信息
     */
    Object getProductStatistics();

    /**
     * 获取库存不足的产品
     */
    List<ProductDTO> getLowStockProducts(Integer threshold);

    /**
     * 获取热销产品
     */
    List<ProductDTO> getTopSellingProducts(Integer limit);

    /**
     * 获取产品分类列表
     */
    List<Map<String, Object>> getProductCategories();

    /**
     * 更新产品库存
     */
    boolean updateProductStock(Long id, Integer stock);

    /**
     * 批量更新产品库存
     */
    int batchUpdateProductStock(List<Map<String, Object>> stockUpdates);

    /**
     * 检查产品名称是否存在
     */
    boolean existsByName(String name, Long excludeId);

    /**
     * 检查产品编码是否存在
     */
    boolean existsByCode(String code, Long excludeId);

    /**
     * 获取今日新增产品数量
     */
    long getTodayNewProductCount();

    /**
     * 获取本周新增产品数量
     */
    long getWeekNewProductCount();

    /**
     * 获取本月新增产品数量
     */
    long getMonthNewProductCount();

    /**
     * 导出产品数据
     */
    List<ProductDTO> exportProducts();

    /**
     * 复制产品
     */
    ProductDTO copyProduct(Long id, String newName);
    
    /**
     * 快速搜索产品
     */
    List<Map<String, Object>> quickSearchProducts(String keyword, Integer limit);
}
