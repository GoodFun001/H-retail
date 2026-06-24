package com.retail.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.retail.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    boolean register(User user);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 根据手机号查询用户
     */
    User getByPhone(String phone);

    /**
     * 升级VIP
     */
    boolean upgradeVip(Long userId);

    /**
     * 更新用户信息
     */
    boolean updateUserInfo(User user);

    /**
     * 检查用户是否为VIP
     */
    boolean isVipUser(Long userId);
}
