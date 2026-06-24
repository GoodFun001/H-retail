package com.retail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建订单DTO
 */
@Data
@Schema(description = "创建订单请求")
public class OrderCreateDTO {

    @NotNull(message = "收货地址不能为空")
    @Schema(description = "收货地址ID")
    private Long addressId;

    @Valid
    @NotEmpty(message = "订单项不能为空")
    @Schema(description = "订单项列表")
    private List<OrderItemDTO> items;

    @Schema(description = "订单备注")
    private String remarks;

    @Schema(description = "优惠券ID")
    private Long couponId;

    /**
     * 订单项DTO
     */
    @Data
    @Schema(description = "订单项")
    public static class OrderItemDTO {

        @NotNull(message = "产品ID不能为空")
        @Schema(description = "产品ID")
        private Long productId;

        @NotNull(message = "数量不能为空")
        @Schema(description = "购买数量", minimum = "1")
        private Integer quantity;
    }
}
