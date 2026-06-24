package com.retail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.retail.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AdminProductMapper extends BaseMapper<Product> {
    
    /**
     * 获取所有不同的产品分类
     */
    @Select("SELECT DISTINCT category FROM product WHERE deleted = 0 AND status = 1 ORDER BY category")
    List<Integer> selectDistinctCategories();
}