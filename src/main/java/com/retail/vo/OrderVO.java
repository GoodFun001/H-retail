package com.retail.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderVO {
    private Long id;
    private Long userId;
    private BigDecimal totalPrice;
    private Integer status;
    private String statusText;
    private String shippingAddress;
    private String paymentMethod;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<OrderDetailVO> orderDetails;
}