package com.retail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 订单更新DTO
 */
@Data
@Schema(description = "订单更新请求")
public class OrderUpdateDTO {

    @NotNull(message = "订单ID不能为空")
    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单状态", allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status;

    @Schema(description = "支付状态", allowableValues = {"UNPAID", "PAID", "REFUNDED"})
    private String paymentStatus;

    @Schema(description = "发货状态", allowableValues = {"UNSHIPPED", "SHIPPED", "DELIVERED"})
    private String shippingStatus;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "订单备注")
    private String remarks;

    @Schema(description = "快递单号")
    private String trackingNumber;
}
