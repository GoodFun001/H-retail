package com.retail.controller;

import com.retail.entity.Product;
import com.retail.entity.User;
import com.retail.repository.ProductRepository;
import com.retail.repository.UserRepository;
import com.retail.service.AdminOrderService;
import com.retail.service.ProductService;
import com.retail.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardController 测试")
class AdminDashboardControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private ProductService productService;

    @Mock
    private AdminOrderService adminOrderService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AdminDashboardController controller;

    private List<User> users;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        User normalUser = new User();
        normalUser.setId(1L);
        normalUser.setUsername("user1");
        normalUser.setUserType(0);

        User vipUser = new User();
        vipUser.setId(2L);
        vipUser.setUsername("vip1");
        vipUser.setUserType(1);

        users = Arrays.asList(normalUser, vipUser);

        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("商品A");
        p1.setStock(50);
        p1.setPrice(new BigDecimal("100.00"));

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("商品B");
        p2.setStock(30);
        p2.setPrice(new BigDecimal("200.00"));

        products = Arrays.asList(p1, p2);
    }

    @Test
    @DisplayName("getUserStatistics - 正常返回统计数据")
    void getUserStatistics_Success() {
        when(userRepository.findAll()).thenReturn(users);

        ResponseEntity<Map<String, Object>> response = controller.getUserStatistics();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> stats = response.getBody();
        assertNotNull(stats);
        assertEquals(2, stats.get("totalUsers"));
        assertEquals(1L, stats.get("vipUsers"));
        assertEquals(1L, stats.get("normalUsers"));
    }

    @Test
    @DisplayName("getUserStatistics - 空用户列表")
    void getUserStatistics_Empty() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        ResponseEntity<Map<String, Object>> response = controller.getUserStatistics();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> stats = response.getBody();
        assertNotNull(stats);
        assertEquals(0, stats.get("totalUsers"));
    }

    @Test
    @DisplayName("getSystemOverview - 正常返回概览数据")
    void getSystemOverview_Success() {
        when(userRepository.findAll()).thenReturn(users);
        when(productRepository.count()).thenReturn(100L);
        when(productRepository.findAll()).thenReturn(products);

        Map<String, Object> orderStats = new HashMap<>();
        orderStats.put("totalOrders", 50L);
        orderStats.put("todayOrders", 5L);
        orderStats.put("monthOrders", 20L);
        orderStats.put("totalSales", new BigDecimal("10000.00"));
        orderStats.put("statusCount", new HashMap<>());
        when(adminOrderService.getOrderStatistics()).thenReturn(orderStats);

        ResponseEntity<Map<String, Object>> response = controller.getSystemOverview();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> overview = response.getBody();
        assertNotNull(overview);
        assertEquals(2, overview.get("totalUsers"));
        assertEquals(100L, overview.get("totalProducts"));
        assertEquals(80, overview.get("totalStock"));
        assertEquals(50L, overview.get("totalOrders"));
        assertEquals(5L, overview.get("todayOrders"));
        assertEquals(20L, overview.get("monthOrders"));
    }

    @Test
    @DisplayName("getSystemOverview - 空用户和商品")
    void getSystemOverview_EmptyData() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        when(productRepository.count()).thenReturn(0L);
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        Map<String, Object> orderStats = new HashMap<>();
        orderStats.put("totalOrders", 0L);
        orderStats.put("todayOrders", 0L);
        orderStats.put("monthOrders", 0L);
        orderStats.put("totalSales", BigDecimal.ZERO);
        orderStats.put("statusCount", Collections.emptyMap());
        when(adminOrderService.getOrderStatistics()).thenReturn(orderStats);

        ResponseEntity<Map<String, Object>> response = controller.getSystemOverview();
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> overview = response.getBody();
        assertNotNull(overview);
        assertEquals(0, overview.get("totalUsers"));
        assertEquals(0L, overview.get("totalProducts"));
        assertEquals(0, overview.get("totalStock"));
    }
}
