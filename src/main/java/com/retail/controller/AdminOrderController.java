package com.retail.controller;

import com.retail.common.Result;
import com.retail.entity.Order;
import com.retail.service.AdminOrderService;
import com.retail.vo.AdminOrderDetailVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员订单管理控制器
 * 
 * 【重构说明】移除了 Controller 对 AdminOrderMapper 的直接注入，
 * 所有数据访问操作统一通过 AdminOrderService 完成，符合分层架构原则。
 */
@RestController
@RequestMapping("/admin/orders")
@Slf4j
@Tag(name = "管理员订单管理", description = "管理员订单管理接口")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    /**
     * 获取订单列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取订单列表", description = "分页获取所有订单")
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Map<String, Object> result = new HashMap<>();
            List<Order> orders = adminOrderService.getAllOrders();
            result.put("records", orders);
            result.put("total", orders.size());
            result.put("current", current);
            result.put("size", size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("获取订单列表失败: {}", e.getMessage(), e);
            return Result.error("获取订单列表失败");
        }
    }

    /**
     * 搜索订单
     */
    @GetMapping("/search")
    @Operation(summary = "搜索订单", description = "根据订单编号、用户ID等搜索订单")
    public Result<List<Order>> searchOrders(
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId) {
        try {
            List<Order> orders = adminOrderService.searchOrders(orderNo, userId);
            return Result.success(orders);
        } catch (Exception e) {
            log.error("搜索订单失败: {}", e.getMessage(), e);
            return Result.error("搜索订单失败");
        }
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取订单详情", description = "根据ID获取订单详情")
    public Result<AdminOrderDetailVO> getOrderDetail(@PathVariable Long id) {
        try {
            AdminOrderDetailVO vo = adminOrderService.getAdminOrderDetail(id);
            return Result.success(vo);
        } catch (RuntimeException e) {
            log.error("获取订单详情失败: {}", e.getMessage(), e);
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("获取订单详情失败: {}", e.getMessage(), e);
            return Result.error("获取订单详情失败");
        }
    }
    
    /**
     * 更新订单
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新订单", description = "更新订单信息")
    public Result<Order> updateOrder(@PathVariable Long id, @RequestBody Map<String, Object> updateData) {
        try {
            Order updatedOrder = adminOrderService.adminUpdateOrder(id, updateData);
            return Result.success(updatedOrder);
        } catch (RuntimeException e) {
            log.error("更新订单失败: {}", e.getMessage(), e);
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            log.error("更新订单失败: {}", e.getMessage(), e);
            return Result.error("更新订单失败: " + e.getMessage());
        }
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除订单", description = "删除订单")
    public Result<String> deleteOrder(@PathVariable Long id) {
        try {
            adminOrderService.deleteOrder(id);
            return Result.success("删除订单成功");
        } catch (Exception e) {
            log.error("删除订单失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 发货
     */
    @PostMapping("/{id}/ship")
    @Operation(summary = "发货", description = "标记订单为已发货")
    public Result<String> shipOrder(@PathVariable Long id) {
        try {
            adminOrderService.shipOrder(id);
            return Result.success("发货成功");
        } catch (Exception e) {
            log.error("发货失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 退款
     */
    @PostMapping("/{id}/refund")
    @Operation(summary = "退款", description = "标记订单为已退款")
    public Result<String> refundOrder(@PathVariable Long id) {
        try {
            adminOrderService.refundOrder(id);
            return Result.success("退款成功");
        } catch (Exception e) {
            log.error("退款失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 订单统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "订单统计", description = "获取订单统计信息")
    public Result<Map<String, Object>> getOrderStatistics() {
        try {
            Map<String, Object> statistics = adminOrderService.getOrderStatistics();
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取订单统计失败: {}", e.getMessage(), e);
            return Result.error("获取订单统计失败");
        }
    }
}