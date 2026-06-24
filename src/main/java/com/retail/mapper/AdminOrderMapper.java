package com.retail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.retail.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminOrderMapper extends BaseMapper<Order> {
    // 可以在这里添加管理员相关的自定义查询方法
}