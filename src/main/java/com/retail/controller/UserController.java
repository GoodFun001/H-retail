package com.retail.controller;

import com.retail.common.Result;
import com.retail.entity.User;
import com.retail.service.UserService;
import com.retail.util.JwtUtil;
import com.retail.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "用户管理", description = "用户注册、登录、信息管理相关接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<String> register(@Valid @RequestBody User user) {
        try {
            boolean success = userService.register(user);
            if (success) {
                return Result.success("注册成功");
            } else {
                return Result.error(400, "注册失败，用户名或手机号已存在");
            }
        } catch (Exception e) {
            log.error("用户注册异常: {}", e.getMessage(), e);
            return Result.error("注册失败，请稍后重试");
        }
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT Token")
    public Result<String> login(
            @Parameter(description = "登录信息", required = true) @RequestBody LoginRequest loginRequest) {
        try {
            String token = userService.login(loginRequest.getUsername(), loginRequest.getPassword());
            if (token != null) {
                return Result.success("登录成功", token);
            } else {
                return Result.error("用户名或密码错误");
            }
        } catch (Exception e) {
            log.error("用户登录异常: {}", e.getMessage(), e);
            return Result.error("登录失败，请稍后重试");
        }
    }

    // 内部静态类，用于接收登录请求参数
    private static class LoginRequest {
        @NotBlank(message = "用户名不能为空")
        private String username;
        
        @NotBlank(message = "密码不能为空")
        private String password;
        
        // getter and setter
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public Result<User> getUserInfo() {
        try {
            User user = getCurrentUser();
            if (user != null) {
                // 清除敏感信息
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.unauthorized("用户未登录");
            }
        } catch (Exception e) {
            log.error("获取用户信息异常: {}", e.getMessage(), e);
            return Result.error("获取用户信息失败");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的信息")
    public Result<String> updateUserInfo(@Valid @RequestBody User user) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return Result.unauthorized("用户未登录");
            }

            // 设置用户ID，防止修改其他用户信息
            user.setId(currentUser.getId());
            
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息异常: {}", e.getMessage(), e);
            return Result.error("更新失败，请稍后重试");
        }
    }

    /**
     * 升级VIP
     */
    @PostMapping("/upgrade-vip")
    @Operation(summary = "升级VIP", description = "将当前用户升级为VIP用户")
    public Result<String> upgradeVip() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return Result.unauthorized("用户未登录");
            }

            // 检查是否已经是VIP
            if (userService.isVipUser(currentUser.getId())) {
                return Result.error("您已经是VIP用户");
            }

            boolean success = userService.upgradeVip(currentUser.getId());
            if (success) {
                return Result.success("升级VIP成功");
            } else {
                return Result.error("升级VIP失败");
            }
        } catch (Exception e) {
            log.error("升级VIP异常: {}", e.getMessage(), e);
            return Result.error("升级VIP失败，请稍后重试");
        }
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "用户退出登录，清除会话信息")
    public Result<String> logout(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token != null) {
                // 从Redis中删除用户会话
                String sessionKey = "user:session:" + token;
                redisUtil.delete(sessionKey);
                log.info("用户退出登录: {}", token);
            }
            return Result.success("退出登录成功");
        } catch (Exception e) {
            log.error("退出登录异常: {}", e.getMessage(), e);
            return Result.error("退出登录失败");
        }
    }

    /**
     * 检查VIP状态
     */
    @GetMapping("/vip-status")
    @Operation(summary = "检查VIP状态", description = "检查当前用户的VIP状态")
    public Result<Boolean> checkVipStatus() {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return Result.unauthorized("用户未登录");
            }

            boolean isVip = userService.isVipUser(currentUser.getId());
            return Result.success(isVip);
        } catch (Exception e) {
            log.error("检查VIP状态异常: {}", e.getMessage(), e);
            return Result.error("检查VIP状态失败");
        }
    }

    /**
     * 从SecurityContext中获取当前用户
     */
    private User getCurrentUser() {
        try {
            // 从SecurityContext中获取用户信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || authentication.getPrincipal() == null) {
                return null;
            }

            // 检查principal是否为User对象
            if (authentication.getPrincipal() instanceof User) {
                return (User) authentication.getPrincipal();
            }
            
            return null;
        } catch (Exception e) {
            log.error("获取当前用户失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return null;
    }
}
