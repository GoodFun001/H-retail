package com.retail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.*;
import java.math.BigDecimal;

/**
 * 产品创建DTO
 */
@Data
@Schema(description = "产品创建请求")
public class ProductCreateDTO {

    @Schema(description = "产品名称", required = true)
    @NotBlank(message = "产品名称不能为空")
    @Size(max = 100, message = "产品名称长度不能超过100个字符")
    private String name;

    @Schema(description = "产品编码")
    @Size(max = 50, message = "产品编码长度不能超过50个字符")
    private String code;

    @Schema(description = "产品描述")
    @Size(max = 500, message = "产品描述长度不能超过500个字符")
    private String description;

    @Schema(description = "产品分类", required = true)
    @NotBlank(message = "产品分类不能为空")
    @Size(max = 50, message = "产品分类长度不能超过50个字符")
    private String category;

    @Schema(description = "产品价格", required = true)
    @NotNull(message = "产品价格不能为空")
    @DecimalMin(value = "0.01", message = "产品价格必须大于0")
    @Digits(integer = 10, fraction = 2, message = "价格格式不正确")
    private BigDecimal price;

    @Schema(description = "产品库存", required = true)
    @NotNull(message = "产品库存不能为空")
    @Min(value = 0, message = "产品库存不能小于0")
    private Integer stock;

    @Schema(description = "产品图片URL")
    @Size(max = 500, message = "图片URL长度不能超过500个字符")
    private String image;

    @Schema(description = "产品规格")
    @Size(max = 200, message = "产品规格长度不能超过200个字符")
    private String specifications;

    @Schema(description = "品牌")
    @Size(max = 100, message = "品牌长度不能超过100个字符")
    private String brand;

    @Schema(description = "重量(kg)")
    @DecimalMin(value = "0", message = "重量不能小于0")
    @Digits(integer = 10, fraction = 3, message = "重量格式不正确")
    private BigDecimal weight;

    @Schema(description = "标签（逗号分隔）")
    @Size(max = 200, message = "标签长度不能超过200个字符")
    private String tags;
}
