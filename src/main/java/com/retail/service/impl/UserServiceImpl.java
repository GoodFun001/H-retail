package com.retail.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.retail.entity.User;
import com.retail.mapper.UserMapper;
import com.retail.repository.UserRepository;
import com.retail.service.UserService;
import com.retail.util.JwtUtil;
import com.retail.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 * 【重构说明】引入Repository层，Service通过UserRepository访问数据，
 * 不再直接依赖UserMapper，符合分层架构原则。
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${retail.user-session-time}")
    private Integer userSessionTime;

    @Override
    public boolean register(User user) {
        try {
            if (getByUsername(user.getUsername()) != null) {
                log.warn("用户名已存在: {}", user.getUsername());
                return false;
            }
            if (getByPhone(user.getPhone()) != null) {
                log.warn("手机号已存在: {}", user.getPhone());
                return false;
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUserType(0);
            user.setStatus(1);
            if (user.getEmail() == null) user.setEmail("");

            boolean result = save(user);
            if (result) log.info("用户注册成功: {}", user.getUsername());
            return result;
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String login(String username, String password) {
        try {
            User user = getByUsername(username);
            if (user == null || !passwordEncoder.matches(password, user.getPassword()) || user.getStatus() != 1) {
                log.warn("用户登录失败: {}", username);
                return null;
            }

            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getUserType());
            
            // 1. 存储 token -> userInfo 的映射，使用统一的user:session:前缀
            String tokenKey = "user:session:" + token;
            redisUtil.set(tokenKey, user, userSessionTime, TimeUnit.HOURS);
            
            // 2. 存储 userId -> token 的映射，用于后续更新和登出
            String userIdKey = "user:id:" + user.getId();
            redisUtil.set(userIdKey, token, userSessionTime, TimeUnit.HOURS);
            
            log.info("用户登录成功，已建立双向映射。用户ID: {}, Token: {}", user.getId(), token);
            return token;
        } catch (Exception e) {
            log.error("用户登录失败: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Override
    public boolean upgradeVip(Long userId) {
        try {
            User user = getById(userId);
            if (user == null) return false;

            user.setUserType(1);
            user.setVipExpireTime(LocalDateTime.now().plusYears(1));

            boolean result = updateById(user);
            if (result) {
                log.info("用户升级VIP成功: {}", userId);
                updateUserSession(userId, user);
            }
            return result;
        } catch (Exception e) {
            log.error("用户升级VIP失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean updateUserInfo(User user) {
        try {
            boolean result = updateById(user);
            if (result) {
                log.info("用户信息更新成功: {}", user.getId());
                updateUserSession(user.getId(), user);
            }
            return result;
        } catch (Exception e) {
            log.error("用户信息更新失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean isVipUser(Long userId) {
        try {
            User user = getById(userId);
            return user != null && user.getUserType() == 1 && user.getVipExpireTime() != null && user.getVipExpireTime().isAfter(LocalDateTime.now());
        } catch (Exception e) {
            log.error("检查VIP状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 更新Redis中的用户会话信息
     */
    private void updateUserSession(Long userId, User updatedUser) {
        try {
            // 1. 通过 userId -> token 的映射关系，获取当前用户的 token
            String userIdKey = "user:id:" + userId;
            String token = (String) redisUtil.get(userIdKey);

            if (token == null) {
                log.warn("未能更新用户会话，未找到登录状态。用户ID: {}", userId);
                return;
            }

            // 2. 使用获取到的 token，构建存储用户信息的 key
            String tokenKey = "user:session:" + token;

            // 3. 更新该 key 对应的 value 为最新的用户信息，并重置过期时间
            redisUtil.set(tokenKey, updatedUser, userSessionTime, TimeUnit.HOURS);

            log.info("用户会话信息已成功更新。用户ID: {}", userId);

        } catch (Exception e) {
            log.error("更新用户会话失败: {}", e.getMessage(), e);
        }
    }
}