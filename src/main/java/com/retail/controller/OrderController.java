package com.retail.controller;

import com.retail.common.Result;
import com.retail.entity.Order;
import com.retail.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 订单控制器
 */
@Slf4j
@RestController
@RequestMapping("/orders")
@Tag(name = "订单管理", description = "订单创建、查询、管理相关接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    @Operation(summary = "创建订单", description = "创建新订单")
    public Result<Order> createOrder(@RequestBody Order order,
                                    HttpServletRequest request) {
        try {
            // 调试：打印接收到的订单数据
            log.info("接收到的订单数据: {}", order);
            
            // 从请求中获取用户ID（实际项目中应该从JWT token中获取）
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }
            order.setUserId(userId);

            Order createdOrder = orderService.createOrder(order);
            return Result.success(createdOrder);
        } catch (Exception e) {
            log.error("创建订单失败: {}", e.getMessage(), e);
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户订单列表
     */
    @GetMapping
    @Operation(summary = "获取用户订单列表", description = "获取当前用户的订单列表")
    public Result<List<Order>> getUserOrders(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }

            List<Order> orders = orderService.getOrdersByUserId(userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return Result.error("获取订单列表失败");
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据订单ID获取订单详细信息")
    public Result<Order> getOrderById(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }

            Order order = orderService.getOrderById(id);
            if (order != null) {
                return Result.success(order);
            } else {
                return Result.error("订单不存在");
            }
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage(), e);
            return Result.error("获取订单详情失败");
        }
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    @Operation(summary = "取消订单", description = "取消指定的订单")
    public Result<String> cancelOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }

            boolean success = orderService.cancelOrder(id);
            if (success) {
                return Result.success("订单已取消");
            } else {
                return Result.error("取消订单失败");
            }
        } catch (Exception e) {
            log.error("取消订单失败: {}", e.getMessage(), e);
            return Result.error("取消订单失败");
        }
    }

    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm")
    @Operation(summary = "确认收货", description = "确认收货，完成订单")
    public Result<String> confirmOrder(
            @Parameter(description = "订单ID", required = true) @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }

            // 调用updateOrderStatus方法将订单状态更新为"已完成"（假设状态码3表示已完成）
            boolean success = orderService.updateOrderStatus(id, 3);
            if (success) {
                return Result.success("确认收货成功");
            } else {
                return Result.error("确认收货失败");
            }
        } catch (Exception e) {
            log.error("确认收货失败: {}", e.getMessage(), e);
            return Result.error("确认收货失败");
        }
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取订单统计", description = "获取用户订单统计信息")
    public Result<Map<String, Object>> getOrderStatistics(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.unauthorized("用户未登录");
            }

            // 暂时返回空的统计信息，实际功能需要在OrderService中实现
            Map<String, Object> statistics = new java.util.HashMap<>();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取订单统计失败: {}", e.getMessage(), e);
            return Result.error("获取订单统计失败");
        }
    }

    /**
     * 从SecurityContext中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() != null) {
                Object principal = authentication.getPrincipal();
                
                // 处理User实体对象情况
                if (principal instanceof com.retail.entity.User) {
                    com.retail.entity.User user = (com.retail.entity.User) principal;
                    return user.getId();
                }
                
                // 保留对Map类型的支持（兼容旧版本）
                if (principal instanceof Map) {
                    Map<String, Object> userInfo = (Map<String, Object>) principal;
                    Number userIdNum = (Number) userInfo.get("id");
                    if (userIdNum != null) {
                        return userIdNum.longValue();
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取用户ID失败: {}", e.getMessage());
        }
        return null;
    }
}
