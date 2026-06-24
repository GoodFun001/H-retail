package com.retail.common;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 订单状态枚举 - 统一的订单状态定义
 * 解决原代码中 AdminOrderController 和 AdminOrderServiceImpl 各自维护状态映射的问题
 */
public enum OrderStatusEnum {
    PENDING_PAYMENT(0, "待支付"),
    PAID(1, "已支付"),
    SHIPPED(2, "已发货"),
    COMPLETED(3, "已完成"),
    CANCELLED(4, "已取消"),
    REFUNDED(5, "已退款");

    private final Integer code;
    private final String desc;

    private static final Map<Integer, OrderStatusEnum> CODE_MAP = 
        Arrays.stream(values()).collect(Collectors.toMap(OrderStatusEnum::getCode, e -> e));

    private static final Map<String, OrderStatusEnum> NAME_MAP = 
        Arrays.stream(values()).collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    OrderStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据状态码获取枚举
     */
    public static OrderStatusEnum fromCode(Integer code) {
        return CODE_MAP.getOrDefault(code, null);
    }

    /**
     * 根据名称获取枚举（支持大小写不敏感）
     */
    public static OrderStatusEnum fromName(String name) {
        if (name == null) return null;
        return NAME_MAP.getOrDefault(name.toLowerCase(), null);
    }

    /**
     * 根据字符串状态获取状态码（兼容旧的状态名称映射）
     */
    public static Integer resolveCode(String status) {
        if (status == null) return null;
        try {
            return Integer.parseInt(status);
        } catch (NumberFormatException e) {
            // 尝试匹配旧的状态名称
            switch (status.toUpperCase()) {
                case "PENDING":
                case "PENDING_PAYMENT": return PENDING_PAYMENT.getCode();
                case "PAID":
                case "PROCESSING": return PAID.getCode();
                case "SHIPPED": return SHIPPED.getCode();
                case "COMPLETED":
                case "DELIVERED": return COMPLETED.getCode();
                case "CANCELLED": return CANCELLED.getCode();
                case "REFUNDED": return REFUNDED.getCode();
                default: return null;
            }
        }
    }

    /**
     * 获取状态描述文本
     */
    public static String getDescByCode(Integer code) {
        OrderStatusEnum status = fromCode(code);
        return status != null ? status.getDesc() : "未知";
    }
}
