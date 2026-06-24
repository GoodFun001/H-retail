package com.retail.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String image;
    private Integer status;
    private String category;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}