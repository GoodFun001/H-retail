package com.retail.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.retail.common.OrderStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 * 【重构说明】使用统一的 OrderStatusEnum 替代硬编码状态码
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_info")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 商品ID
     */
    @TableField("product_id")
    private Long productId;

    /**
     * 商品名称（冗余字段，避免关联查询）
     */
    @TableField("product_name")
    private String productName;

    /**
     * 商品价格（下单时价格）
     */
    @TableField("product_price")
    private BigDecimal productPrice;

    /**
     * 购买数量
     */
    @TableField("quantity")
    private Integer quantity;

    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 实付金额（考虑VIP折扣）
     */
    @TableField("pay_amount")
    private BigDecimal payAmount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-已退款
     */
    @TableField("status")
    private Integer status;

    /**
     * 支付方式：1-支付宝，2-微信，3-银行卡
     */
    @TableField("pay_method")
    private Integer payMethod;

    /**
     * 支付时间
     */
    @TableField("pay_time")
    private LocalDateTime payTime;

    /**
     * 收货地址
     */
    @TableField("address")
    private String address;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 收货人
     */
    @TableField("receiver_name")
    private String receiverName;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

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
     * 获取状态名称（使用统一的OrderStatusEnum）
     */
    public String getStatusName() {
        return OrderStatusEnum.getDescByCode(this.status);
    }

    /**
     * 获取支付方式名称
     */
    public String getPayMethodName() {
        switch (this.payMethod) {
            case 1:
                return "支付宝";
            case 2:
                return "微信";
            case 3:
                return "银行卡";
            default:
                return "未知";
        }
    }
}
