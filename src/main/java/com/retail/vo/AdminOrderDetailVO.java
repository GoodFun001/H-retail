package com.retail.vo;

import com.retail.entity.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员订单详情VO
 */
@Data
public class AdminOrderDetailVO {
    private Long id;
    private String orderNo;
    private Long userId;
    private String receiverName;
    private String phone;
    private String address;
    private BigDecimal payAmount;
    private BigDecimal totalAmount;
    private Integer payMethod;
    private Integer status;
    private String statusName;
    private String payMethodName;
    private LocalDateTime createTime;
    private LocalDateTime payTime;
    private LocalDateTime updateTime;
    private Integer deleted;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;

    /**
     * 将Order实体转换为AdminOrderDetailVO（null-safe）
     */
    public static AdminOrderDetailVO fromOrder(Order order) {
        AdminOrderDetailVO vo = new AdminOrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setReceiverName(order.getReceiverName());
        vo.setPhone(order.getPhone());
        vo.setAddress(order.getAddress());
        vo.setPayAmount(order.getPayAmount());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayMethod(order.getPayMethod());
        vo.setStatus(order.getStatus());
        vo.setStatusName(order.getStatusName());
        vo.setPayMethodName(order.getPayMethod() != null ? order.getPayMethodName() : null);
        vo.setCreateTime(order.getCreateTime());
        vo.setPayTime(order.getPayTime());
        vo.setUpdateTime(order.getUpdateTime());
        vo.setDeleted(order.getDeleted());
        vo.setProductId(order.getProductId());
        vo.setProductName(order.getProductName());
        vo.setProductPrice(order.getProductPrice());
        vo.setQuantity(order.getQuantity());
        return vo;
    }
}
