package com.retail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 产品搜索DTO
 */
@Data
@Schema(description = "产品搜索请求")
public class ProductSearchDTO {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "产品分类")
    private String category;

    @Schema(description = "品牌")
    private String brand;

    @Schema(description = "最低价格")
    private java.math.BigDecimal minPrice;

    @Schema(description = "最高价格")
    private java.math.BigDecimal maxPrice;

    @Schema(description = "是否只显示有库存的商品")
    private Boolean inStockOnly;

    @Schema(description = "标签")
    private String tags;

    @Schema(description = "页码", example = "1")
    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer size = 10;

    @Schema(description = "排序字段", allowableValues = {"name", "price", "stock", "createTime", "sales"})
    private String sortBy = "createTime";

    @Schema(description = "排序方向", allowableValues = {"asc", "desc"})
    private String sortDirection = "desc";
}
