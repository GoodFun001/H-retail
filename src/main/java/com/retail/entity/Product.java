package com.retail.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 商品名称
     */
    @TableField("name")
    private String name;

    /**
     * 商品编码
     */
    @TableField(exist = false)
    private String code;

    /**
     * 商品描述
     */
    @TableField("description")
    private String description;

    /**
     * 商品价格
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 商品库存
     */
    @TableField("stock")
    private Integer stock;

    /**
     * 商品图片URL
     */
    @TableField("image_url")
    private String imageUrl;

    /**
     * 商品分类：1-电子产品，2-服装，3-食品，4-图书，5-其他
     */
    @TableField("category")
    private Integer category;

    /**
     * 商品状态：0-下架，1-上架
     */
    @TableField("status")
    private Integer status;

    /**
     * 销量
     */
    @TableField("sales_count")
    private Integer salesCount;

    /**
     * 兼容前端sales字段
     */
    public Integer getSales() {
        return salesCount;
    }

    /**
     * 兼容前端sales字段
     */
    public void setSales(Integer sales) {
        this.salesCount = sales;
    }

    /**
     * 创建者ID（管理员）
     */
    @TableField("create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除：0-未删除，1-已删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;

    /**
     * 获取分类名称
     */
    public String getCategoryName() {
        switch (this.category) {
            case 1:
                return "电子产品";
            case 2:
                return "服装";
            case 3:
                return "食品";
            case 4:
                return "图书";
            case 5:
                return "其他";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        switch (this.status) {
            case 0:
                return "下架";
            case 1:
                return "上架";
            default:
                return "未知";
        }
    }
}
