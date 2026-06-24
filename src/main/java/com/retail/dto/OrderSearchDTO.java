package com.retail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单搜索DTO
 */
@Data
@Schema(description = "订单搜索条件")
public class OrderSearchDTO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名（模糊搜索）")
    private String username;

    @Schema(description = "订单状态", allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "支付状态", allowableValues = {"UNPAID", "PAID", "REFUNDED"})
    private String paymentStatus;

    @Schema(description = "发货状态", allowableValues = {"UNSHIPPED", "SHIPPED", "DELIVERED"})
    private String shippingStatus;

    @Schema(description = "创建时间开始")
    private String createTimeStart;

    @Schema(description = "创建时间结束")
    private String createTimeEnd;

    @Schema(description = "页码", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "排序字段", defaultValue = "createTime")
    private String sortBy = "createTime";

    @Schema(description = "排序方向", allowableValues = {"ASC", "DESC"}, defaultValue = "DESC")
    private String sortDirection = "DESC";
}
