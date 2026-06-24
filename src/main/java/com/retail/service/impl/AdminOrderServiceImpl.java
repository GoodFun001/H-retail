package com.retail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.common.OrderStatusEnum;
import com.retail.dto.OrderCreateDTO;
import com.retail.dto.OrderSearchDTO;
import com.retail.dto.OrderUpdateDTO;
import com.retail.entity.Order;
import com.retail.entity.Product;
import com.retail.repository.AdminOrderRepository;
import com.retail.repository.ProductRepository;
import com.retail.service.AdminOrderService;
import com.retail.vo.AdminOrderDetailVO;
import com.retail.vo.OrderDetailVO;
import com.retail.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员订单服务实现类
 * 
 * 【重构说明】
 * 1. 使用统一的 OrderStatusEnum 替代内部枚举
 * 2. 新增 getAdminOrderDetail() / adminUpdateOrder() / searchOrders(String,Long) 方法
 * 3. 将原 Controller 中的数据操作逻辑移至 Service 层
 * 4. 引入Repository层，Service通过AdminOrderRepository/ProductRepository访问数据
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOrderServiceImpl implements AdminOrderService {

    private final AdminOrderRepository adminOrderRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public OrderVO createOrder(OrderCreateDTO createDTO) {
        // 简化实现，只处理第一个订单项
        if (createDTO.getItems() == null || createDTO.getItems().isEmpty()) {
            throw new RuntimeException("订单项不能为空");
        }
        
        OrderCreateDTO.OrderItemDTO itemDTO = createDTO.getItems().get(0);
        log.info("管理员创建订单，用户ID: 1, 商品ID: {}, 数量: {}", itemDTO.getProductId(), itemDTO.getQuantity());

        // 检查商品是否存在且有足够库存
        Product product = productRepository.selectById(itemDTO.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        if (product.getStock() < itemDTO.getQuantity()) {
            throw new RuntimeException("商品库存不足");
        }

        // 计算总价
        BigDecimal totalPrice = product.getPrice().multiply(new BigDecimal(itemDTO.getQuantity()));

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        // 暂时硬编码用户ID为1，因为OrderCreateDTO中没有userId字段
        order.setUserId(1L);
        order.setProductId(itemDTO.getProductId());
        order.setQuantity(itemDTO.getQuantity());
        order.setTotalAmount(totalPrice);
        order.setStatus(OrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());

        adminOrderRepository.insert(order);

        // 扣减商品库存
        product.setStock(product.getStock() - itemDTO.getQuantity());
        productRepository.updateById(product);

        log.info("订单创建成功，订单号: {}", order.getOrderNo());
        return convertToVO(order);
    }

    @Override
    public OrderDetailVO getOrderById(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return convertToDetailVO(order);
    }

    @Override
    public Map<String, Object> searchOrders(OrderSearchDTO searchDTO) {
        log.info("搜索订单，条件: {}", searchDTO);

        // 构建查询条件
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .eq(searchDTO.getUserId() != null, Order::getUserId, searchDTO.getUserId())
                .like(searchDTO.getOrderNo() != null && !searchDTO.getOrderNo().isEmpty(), 
                      Order::getOrderNo, searchDTO.getOrderNo())
                .orderByDesc(Order::getCreateTime);

        // 处理订单状态
        if (searchDTO.getStatus() != null && !searchDTO.getStatus().isEmpty()) {
            Integer statusCode = convertOrderStatus(searchDTO.getStatus());
            if (statusCode != null) {
                queryWrapper.eq(Order::getStatus, statusCode);
            }
        }

        // 分页查询
        Page<Order> page = new Page<>(searchDTO.getPage(), searchDTO.getSize());
        IPage<Order> pageResult = adminOrderRepository.selectPage(page, queryWrapper);

        // 转换结果
        Map<String, Object> result = new HashMap<>();
        result.put("records", pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList()));
        result.put("total", pageResult.getTotal());
        result.put("current", pageResult.getCurrent());
        result.put("size", pageResult.getSize());
        result.put("pages", pageResult.getPages());

        return result;
    }

    /**
     * 将字符串状态转换为整数状态码（使用统一的OrderStatusEnum）
     */
    private Integer convertOrderStatus(String status) {
        return OrderStatusEnum.resolveCode(status);
    }

    @Override
    @Transactional
    public OrderVO updateOrder(Long id, OrderUpdateDTO updateDTO) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 检查订单状态是否允许修改
        if (order.getStatus() == OrderStatusEnum.COMPLETED.getCode() || 
            order.getStatus() == OrderStatusEnum.CANCELLED.getCode()) {
            throw new RuntimeException("订单已完成或已取消，无法修改");
        }

        // 更新数量（需要重新计算价格和检查库存）
        if (updateDTO.getQuantity() != null && !updateDTO.getQuantity().equals(order.getQuantity())) {
            Product product = productRepository.selectById(order.getProductId());
            
            // 恢复原库存
            int currentStock = product.getStock() + order.getQuantity();
            
            // 检查新库存是否足够
            if (currentStock < updateDTO.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }
            
            // 更新库存
            product.setStock(currentStock - updateDTO.getQuantity());
            productRepository.updateById(product);
            
            // 更新订单数量和总价
            order.setQuantity(updateDTO.getQuantity());
            order.setTotalAmount(product.getPrice().multiply(new BigDecimal(updateDTO.getQuantity())));
        }

        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);

        log.info("订单更新成功，ID: {}", id);
        return convertToVO(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() == OrderStatusEnum.CANCELLED.getCode()) {
            throw new RuntimeException("订单已取消");
        }

        if (order.getStatus() == OrderStatusEnum.COMPLETED.getCode()) {
            throw new RuntimeException("订单已完成，无法取消");
        }

        // 恢复商品库存
        Product product = productRepository.selectById(order.getProductId());
        if (product != null) {
            product.setStock(product.getStock() + order.getQuantity());
            productRepository.updateById(product);
        }

        // 更新订单状态
        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);

        log.info("订单取消成功，ID: {}", id);
    }

    @Override
    @Transactional
    public void confirmPayment(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
            throw new RuntimeException("订单状态不正确，无法确认支付");
        }

        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);

        log.info("订单支付确认成功，ID: {}", id);
    }

    @Override
    @Transactional
    public void shipOrder(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != OrderStatusEnum.PAID.getCode()) {
            throw new RuntimeException("订单未支付，无法发货");
        }

        order.setStatus(OrderStatusEnum.SHIPPED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);

        log.info("订单发货成功，ID: {}", id);
    }

    @Override
    @Transactional
    public void completeOrder(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() != OrderStatusEnum.SHIPPED.getCode()) {
            throw new RuntimeException("订单未发货，无法完成");
        }

        order.setStatus(OrderStatusEnum.COMPLETED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);

        log.info("订单完成，ID: {}", id);
    }

    @Override
    @Transactional
    public void refundOrder(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        if (order.getStatus() == OrderStatusEnum.CANCELLED.getCode()) {
            throw new RuntimeException("订单已取消");
        }

        if (order.getStatus() == OrderStatusEnum.REFUNDED.getCode()) {
            throw new RuntimeException("订单已退款");
        }

        // 恢复商品库存（如果订单不是已取消状态）
        if (order.getStatus() != OrderStatusEnum.CANCELLED.getCode()) {
            Product product = productRepository.selectById(order.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + order.getQuantity());
                productRepository.updateById(product);
            }
        }

        // 更新订单状态
        order.setStatus(OrderStatusEnum.REFUNDED.getCode());
        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);

        log.info("订单退款成功，ID: {}", id);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        adminOrderRepository.deleteById(id);
        log.info("订单删除成功，ID: {}", id);
    }

    @Override
    public List<Order> getAllOrders() {
        return adminOrderRepository.selectList(null);
    }

    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总订单数
        Long totalOrders = adminOrderRepository.selectCount(null);
        statistics.put("totalOrders", totalOrders);
        
        // 各状态订单数
        Map<String, Long> statusCount = new HashMap<>();
        for (OrderStatusEnum status : OrderStatusEnum.values()) {
            Long count = adminOrderRepository.selectCount(
                new LambdaQueryWrapper<Order>().eq(Order::getStatus, status.getCode())
            );
            statusCount.put(status.name().toLowerCase(), count);
        }
        statistics.put("statusCount", statusCount);
        
        // 今日订单数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long todayOrders = adminOrderRepository.selectCount(
            new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, todayStart)
        );
        statistics.put("todayOrders", todayOrders);
        
        // 本月订单数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        Long monthOrders = adminOrderRepository.selectCount(
            new LambdaQueryWrapper<Order>().ge(Order::getCreateTime, monthStart)
        );
        statistics.put("monthOrders", monthOrders);
        
        // 总销售额（已完成订单）
        // 使用MyBatis Plus的查询方法替代自定义方法
        BigDecimal totalSales = adminOrderRepository.selectObjs(
            new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, OrderStatusEnum.COMPLETED.getCode())
                .select(Order::getTotalAmount)
        ).stream()
        .map(obj -> (BigDecimal) obj)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
        statistics.put("totalSales", totalSales);
        
        log.info("获取订单统计完成");
        return statistics;
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis();
    }

    /**
     * 转换为VO对象（使用统一的OrderStatusEnum）
     */
    private OrderVO convertToVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setUserId(order.getUserId());
        vo.setTotalPrice(order.getTotalAmount());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setUpdateTime(order.getUpdateTime());
        vo.setStatusText(OrderStatusEnum.getDescByCode(order.getStatus()));
        return vo;
    }

    /**
     * 转换为详情VO对象
     */
    private OrderDetailVO convertToDetailVO(Order order) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderId(order.getId()); // 设置orderId为订单ID
        vo.setProductId(order.getProductId());
        vo.setQuantity(order.getQuantity());
        vo.setPrice(order.getProductPrice()); // 商品单价
        vo.setSubtotal(order.getTotalAmount()); // 小计金额
        
        // 获取商品信息
        Product product = productRepository.selectById(order.getProductId());
        if (product != null) {
            vo.setProductName(product.getName());
            // 商品图片字段可能不存在，暂时注释
            // vo.setProductImage(product.getImage());
        }
        
        return vo;
    }

    // ==================== 新增方法（重构：从Controller移至Service层） ====================

    @Override
    public AdminOrderDetailVO getAdminOrderDetail(Long id) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        return AdminOrderDetailVO.fromOrder(order);
    }

    @Override
    public List<Order> searchOrders(String orderNo, Long userId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<Order>()
                .like(orderNo != null && !orderNo.isEmpty(), Order::getOrderNo, orderNo)
                .eq(userId != null, Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        return adminOrderRepository.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public Order adminUpdateOrder(Long id, Map<String, Object> updateData) {
        Order order = adminOrderRepository.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 更新订单基本信息
        if (updateData.containsKey("orderNo")) {
            order.setOrderNo((String) updateData.get("orderNo"));
        }
        if (updateData.containsKey("receiverName")) {
            order.setReceiverName((String) updateData.get("receiverName"));
        }
        if (updateData.containsKey("phone")) {
            order.setPhone((String) updateData.get("phone"));
        }
        if (updateData.containsKey("address")) {
            order.setAddress((String) updateData.get("address"));
        }

        // 处理金额字段
        if (updateData.containsKey("payAmount")) {
            Object val = updateData.get("payAmount");
            order.setPayAmount(val instanceof Number ? 
                BigDecimal.valueOf(((Number) val).doubleValue()) : new BigDecimal(val.toString()));
        }
        if (updateData.containsKey("totalAmount")) {
            Object val = updateData.get("totalAmount");
            order.setTotalAmount(val instanceof Number ? 
                BigDecimal.valueOf(((Number) val).doubleValue()) : new BigDecimal(val.toString()));
        }

        // 处理支付方式
        if (updateData.containsKey("payMethod")) {
            Object val = updateData.get("payMethod");
            if (val instanceof Number) order.setPayMethod(((Number) val).intValue());
            else if (val instanceof String) {
                try { order.setPayMethod(Integer.parseInt((String) val)); } 
                catch (NumberFormatException e) { log.warn("支付方式转换失败: {}", val); }
            }
        }

        // 处理支付时间
        if (updateData.containsKey("payTime")) {
            Object val = updateData.get("payTime");
            if (val instanceof String) {
                try { order.setPayTime(LocalDateTime.parse((String) val, DateTimeFormatter.ISO_LOCAL_DATE_TIME)); } 
                catch (DateTimeParseException e) { log.warn("支付时间转换失败: {}", val); }
            }
        }

        // 更新订单状态
        if (updateData.containsKey("status")) {
            Integer status = OrderStatusEnum.resolveCode(updateData.get("status").toString());
            if (status != null) order.setStatus(status);
        }

        order.setUpdateTime(LocalDateTime.now());
        adminOrderRepository.updateById(order);
        return adminOrderRepository.selectById(id);
    }
}
