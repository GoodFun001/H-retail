package com.retail.service.impl;

import com.retail.entity.Order;
import com.retail.entity.Product;
import com.retail.repository.OrderRepository;
import com.retail.service.OrderService;
import com.retail.service.ProductService;
import com.retail.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 订单服务实现类
 * 【重构说明】引入Repository层，Service通过OrderRepository访问数据，
 * 不再直接依赖OrderMapper，符合分层架构原则。
 */
@Service
public class OrderServiceImpl implements OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public Order createOrder(Order order) {
        try {
            // 1. 验证商品是否存在
            Product product = productService.getProductDetail(order.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            
            // 2. 检查库存是否充足
            Integer requiredQuantity = order.getQuantity();
            if (requiredQuantity == null || requiredQuantity <= 0) {
                throw new RuntimeException("购买数量必须大于0");
            }
            
            // 3. 检查Redis中是否有库存记录，如果没有则从数据库加载
            String stockKey = "product:stock:" + order.getProductId();
            String stockValue = redisUtil.getString(stockKey);
            if (stockValue == null) {
                // Redis中没有库存记录，从数据库加载并初始化到Redis
                redisUtil.setString(stockKey, String.valueOf(product.getStock()));
            }
            
            // 4. 使用Redis进行原子性库存扣减
            Long remainStock = redisUtil.deductStock(stockKey, requiredQuantity);
            
            if (remainStock == null) {
                throw new RuntimeException("库存扣减失败");
            }
            
            if (remainStock < 0) {
                // 库存不足，需要回滚
                redisUtil.addStock(stockKey, requiredQuantity);
                throw new RuntimeException("库存不足");
            }
            
            // 4. 更新数据库中的库存
            product.setStock(product.getStock() - requiredQuantity);
            productService.updateById(product);
            
            // 5. 设置订单信息
            order.setCreateTime(java.time.LocalDateTime.now());
            order.setStatus(0); // 0-待支付
            order.setOrderNo(generateOrderNo());
            order.setProductName(product.getName());
            order.setProductPrice(product.getPrice());
            
            // 6. 计算订单金额
            order.setTotalAmount(product.getPrice().multiply(java.math.BigDecimal.valueOf(requiredQuantity)));
            order.setPayAmount(order.getTotalAmount()); // 默认实付金额等于总金额，后续可根据VIP等级调整
            
            // 7. 通过Repository创建订单
            int result = orderRepository.insert(order);
            if (result > 0) {
                // 8. 订单创建成功，更新商品销量
                product.setSalesCount(product.getSalesCount() + requiredQuantity);
                productService.updateById(product);
                return order;
            }
            
            // 订单创建失败，回滚库存
            redisUtil.addStock(stockKey, requiredQuantity);
            product.setStock(product.getStock() + requiredQuantity);
            productService.updateById(product);
            
            return null;
        } catch (Exception e) {
            // 发生异常时，确保库存回滚
            if (order.getProductId() != null && order.getQuantity() != null) {
                String stockKey = "product:stock:" + order.getProductId();
                redisUtil.addStock(stockKey, order.getQuantity());
            }
            throw e;
        }
    }
    
    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        // 生成格式：SN + 年月日 + 毫秒时间（当天） + 随机数
        LocalDateTime now = LocalDateTime.now();
        String datePrefix = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 获取当天0点的时间戳
        LocalDateTime midnight = now.with(LocalTime.MIDNIGHT);
        long timeSinceMidnight = now.toInstant(ZoneOffset.UTC).toEpochMilli() - midnight.toInstant(ZoneOffset.UTC).toEpochMilli();
        // 生成3位随机数
        int random = (int) (Math.random() * 1000);
        return String.format("SN%s%05d%03d", datePrefix, timeSinceMidnight / 1000, random);
    }
    
    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    @Override
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        return orderRepository.updateStatus(id, status) > 0;
    }
    
    @Override
    public boolean cancelOrder(Long id) {
        return updateOrderStatus(id, 2); // 2-已取消
    }
    
    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
