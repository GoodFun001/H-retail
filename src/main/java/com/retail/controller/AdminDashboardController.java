package com.retail.controller;

import com.retail.entity.Product;
import com.retail.entity.User;
import com.retail.repository.ProductRepository;
import com.retail.repository.UserRepository;
import com.retail.service.AdminOrderService;
import com.retail.service.ProductService;
import com.retail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员仪表盘控制器
 * 【重构说明】引入Repository层，Controller通过Repository获取原始数据用于统计，
 * 减少对Service层复杂接口的依赖。
 */
@RestController
@RequestMapping("/admin/dashboard")
@Slf4j
@Tag(name = "管理员仪表盘", description = "管理员仪表盘统计接口")
public class AdminDashboardController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private AdminOrderService adminOrderService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 获取用户统计信息
     */
    @GetMapping("/user-statistics")
    @Operation(summary = "获取用户统计信息", description = "获取用户总数、VIP用户数量等统计数据")
    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        List<User> users = userRepository.findAll();
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalUsers", users.size());
        
        // 统计VIP用户数量（userType为1表示VIP用户）
        long vipCount = users.stream()
                .filter(user -> user.getUserType() != null && user.getUserType() == 1)
                .count();
        statistics.put("vipUsers", vipCount);
        statistics.put("normalUsers", users.size() - vipCount);
        
        // 统计各用户类型数量
        Map<Integer, Long> userTypeCount = users.stream()
                .collect(java.util.stream.Collectors.groupingBy(User::getUserType, java.util.stream.Collectors.counting()));
        statistics.put("userTypeCount", userTypeCount);
        
        return ResponseEntity.ok(statistics);
    }

    /**
     * 获取系统概览统计
     */
    @GetMapping("/overview")
    @Operation(summary = "获取系统概览统计", description = "获取系统整体概览统计数据")
    public ResponseEntity<Map<String, Object>> getSystemOverview() {
        List<User> users = userRepository.findAll();
        Map<String, Object> overview = new HashMap<>();
        overview.put("totalUsers", users.size());
        
        // 添加商品总数统计（通过Repository）
        Long totalProducts = productRepository.count();
        overview.put("totalProducts", totalProducts);
        
        // 计算总库存（通过Repository）
        List<Product> products = productRepository.findAll();
        int totalStock = products.stream()
                .mapToInt(Product::getStock)
                .sum();
        overview.put("totalStock", totalStock);
        
        // 获取订单统计数据
        Map<String, Object> orderStatistics = adminOrderService.getOrderStatistics();
        
        // 添加订单统计数据
        overview.put("totalOrders", orderStatistics.get("totalOrders"));
        overview.put("todayOrders", orderStatistics.get("todayOrders"));
        overview.put("monthOrders", orderStatistics.get("monthOrders"));
        
        // 添加销售统计数据
        overview.put("totalSales", orderStatistics.get("totalSales"));
        
        // 添加各状态订单数统计
        overview.put("statusCount", orderStatistics.get("statusCount"));
        
        return ResponseEntity.ok(overview);
    }
}