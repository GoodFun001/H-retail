-- 创建数据库
CREATE DATABASE IF NOT EXISTS retail_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE retail_platform;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `user_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '用户类型：0-普通用户，1-VIP用户，2-管理员',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '用户状态：0-禁用，1-启用',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `vip_expire_time` datetime DEFAULT NULL COMMENT 'VIP到期时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_phone` (`phone`),
  KEY `idx_user_type` (`user_type`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `product` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `name` varchar(100) NOT NULL COMMENT '商品名称',
  `description` text COMMENT '商品描述',
  `price` decimal(10,2) NOT NULL COMMENT '商品价格',
  `stock` int(11) NOT NULL DEFAULT '0' COMMENT '商品库存',
  `image_url` varchar(255) DEFAULT NULL COMMENT '商品图片URL',
  `category` tinyint(4) NOT NULL DEFAULT '5' COMMENT '商品分类：1-电子产品，2-服装，3-食品，4-图书，5-其他',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '商品状态：0-下架，1-上架',
  `sales_count` int(11) NOT NULL DEFAULT '0' COMMENT '销量',
  `create_by` bigint(20) DEFAULT NULL COMMENT '创建者ID（管理员）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_category` (`category`),
  KEY `idx_status` (`status`),
  KEY `idx_sales_count` (`sales_count`),
  KEY `idx_create_by` (`create_by`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 订单表
CREATE TABLE IF NOT EXISTS `order_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) NOT NULL COMMENT '商品名称（冗余字段）',
  `product_price` decimal(10,2) NOT NULL COMMENT '商品价格（下单时价格）',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '购买数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '总金额',
  `pay_amount` decimal(10,2) NOT NULL COMMENT '实付金额（考虑VIP折扣）',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态：0-待支付，1-已支付，2-已取消，3-已完成，4-已退款',
  `pay_method` tinyint(4) DEFAULT NULL COMMENT '支付方式：1-支付宝，2-微信，3-银行卡',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `address` varchar(255) DEFAULT NULL COMMENT '收货地址',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `receiver_name` varchar(50) DEFAULT NULL COMMENT '收货人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_product_id` (`product_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 支付记录表
CREATE TABLE IF NOT EXISTS `payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `amount` decimal(10,2) NOT NULL COMMENT '支付金额',
  `pay_method` tinyint(4) NOT NULL COMMENT '支付方式：1-支付宝，2-微信，3-银行卡',
  `pay_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '支付状态：0-待支付，1-支付成功，2-支付失败',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_pay_status` (`pay_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 管理员用户表
CREATE TABLE IF NOT EXISTS `admin_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) NOT NULL COMMENT '角色',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_role` (`role`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 插入管理员用户表初始数据（密码：admin123）
INSERT INTO `admin_users` (`username`, `password`, `real_name`, `email`, `phone`, `role`, `status`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoCeNqdkkd6XtzsLKC1OVtP2dW1y', '系统管理员', 'admin@example.com', '13800138000', 'ADMIN', 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- 插入普通用户表管理员账号（密码：admin123）
INSERT INTO `user` (`username`, `password`, `user_type`, `status`, `real_name`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoCeNqdkkd6XtzsLKC1OVtP2dW1y', 2, 1, '系统管理员')
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- 插入示例商品数据
INSERT INTO `product` (`name`, `description`, `price`, `stock`, `category`, `status`, `sales_count`, `create_by`) VALUES
('iPhone 15 Pro', '苹果最新旗舰手机，搭载A17 Pro芯片，支持5G网络', 8999.00, 100, 1, 1, 50, 1),
('MacBook Pro 16寸', '苹果笔记本电脑，M3 Pro芯片，16GB内存，512GB固态硬盘', 19999.00, 50, 1, 1, 30, 1),
('小米13 Ultra', '小米旗舰手机，骁龙8 Gen2处理器，1英寸超大底主摄', 5999.00, 200, 1, 1, 120, 1),
('华为Mate 60 Pro', '华为旗舰手机，麒麟9000S芯片，卫星通话功能', 6999.00, 150, 1, 1, 80, 1),
('戴尔XPS13', '商务轻薄本，13.4英寸屏幕，Intel i7处理器，16GB内存', 12999.00, 80, 1, 1, 25, 1),
('索尼WH-1000XM5', '索尼降噪耳机，业界领先降噪技术，30小时续航', 2499.00, 300, 1, 1, 200, 1),
('iPad Air 5', '苹果平板电脑，10.9英寸屏幕，M1芯片，64GB存储', 4799.00, 120, 1, 1, 60, 1),
('小米电视75寸', '小米智能电视，4K超高清分辨率，小爱同学语音控制', 3999.00, 60, 1, 1, 90, 1),
('任天堂Switch OLED', '任天堂游戏主机，7英寸OLED屏幕，便携式设计', 2599.00, 180, 1, 1, 150, 1),
('AirPods Pro 2', '苹果无线耳机，主动降噪，空间音频，6小时续航', 1899.00, 250, 1, 1, 180, 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);
