package com.retail.controller;

import com.retail.common.Result;
import com.retail.entity.User;
import com.retail.service.UserService;
import com.retail.util.JwtUtil;
import com.retail.util.RedisUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserController 完整测试")
class UserControllerFullTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPass");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setUserType(0);
        testUser.setStatus(1);

        request = new MockHttpServletRequest();
        SecurityContextHolder.setContext(securityContext);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(testUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("register - 成功")
    void register_Success() {
        when(userService.register(any(User.class))).thenReturn(true);

        Result<String> result = userController.register(testUser);
        assertEquals(200, result.getCode());
        assertEquals("注册成功", result.getData());
    }

    @Test
    @DisplayName("register - 用户名或手机号已存在")
    void register_Duplicate() {
        when(userService.register(any(User.class))).thenReturn(false);

        Result<String> result = userController.register(testUser);
        assertEquals(400, result.getCode());
    }

    @Test
    @DisplayName("register - 异常")
    void register_Exception() {
        when(userService.register(any(User.class))).thenThrow(new RuntimeException("DB error"));

        Result<String> result = userController.register(testUser);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("请稍后重试"));
    }

    @Test
    @DisplayName("getUserInfo - 成功")
    void getUserInfo_Success() {
        Result<User> result = userController.getUserInfo();
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertNull(result.getData().getPassword());
    }

    @Test
    @DisplayName("getUserInfo - 未登录")
    void getUserInfo_Unauthorized() {
        when(authentication.getPrincipal()).thenReturn(null);

        Result<User> result = userController.getUserInfo();
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("getUserInfo - principal不是User实例")
    void getUserInfo_NotUserInstance() {
        when(authentication.getPrincipal()).thenReturn("not a user");

        Result<User> result = userController.getUserInfo();
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("getUserInfo - authentication为null")
    void getUserInfo_NullAuth() {
        when(securityContext.getAuthentication()).thenReturn(null);

        Result<User> result = userController.getUserInfo();
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("getUserInfo - getCurrentUser异常返回null->401")
    void getUserInfo_Exception() {
        // getCurrentUser catches exception and returns null, so controller returns 401
        when(authentication.getPrincipal()).thenThrow(new RuntimeException("error"));

        Result<User> result = userController.getUserInfo();
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("updateUserInfo - 成功")
    void updateUserInfo_Success() {
        User updateUser = new User();
        updateUser.setUsername("newName");
        when(userService.updateUserInfo(any(User.class))).thenReturn(true);

        Result<String> result = userController.updateUserInfo(updateUser);
        assertEquals(200, result.getCode());
        assertEquals("更新成功", result.getData());
    }

    @Test
    @DisplayName("updateUserInfo - 未登录")
    void updateUserInfo_Unauthorized() {
        when(authentication.getPrincipal()).thenReturn(null);

        Result<String> result = userController.updateUserInfo(new User());
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("updateUserInfo - 失败")
    void updateUserInfo_Failed() {
        when(userService.updateUserInfo(any(User.class))).thenReturn(false);

        Result<String> result = userController.updateUserInfo(new User());
        assertEquals(500, result.getCode());
    }

    @Test
    @DisplayName("updateUserInfo - 异常")
    void updateUserInfo_Exception() {
        when(userService.updateUserInfo(any(User.class))).thenThrow(new RuntimeException("error"));

        Result<String> result = userController.updateUserInfo(new User());
        assertEquals(500, result.getCode());
    }

    @Test
    @DisplayName("upgradeVip - 成功")
    void upgradeVip_Success() {
        when(userService.isVipUser(1L)).thenReturn(false);
        when(userService.upgradeVip(1L)).thenReturn(true);

        Result<String> result = userController.upgradeVip();
        assertEquals(200, result.getCode());
        assertEquals("升级VIP成功", result.getData());
    }

    @Test
    @DisplayName("upgradeVip - 未登录")
    void upgradeVip_Unauthorized() {
        when(authentication.getPrincipal()).thenReturn(null);

        Result<String> result = userController.upgradeVip();
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("upgradeVip - 已经是VIP")
    void upgradeVip_AlreadyVip() {
        when(userService.isVipUser(1L)).thenReturn(true);

        Result<String> result = userController.upgradeVip();
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("已经是VIP"));
    }

    @Test
    @DisplayName("upgradeVip - 升级失败")
    void upgradeVip_Failed() {
        when(userService.isVipUser(1L)).thenReturn(false);
        when(userService.upgradeVip(1L)).thenReturn(false);

        Result<String> result = userController.upgradeVip();
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("升级VIP失败"));
    }

    @Test
    @DisplayName("upgradeVip - 异常")
    void upgradeVip_Exception() {
        when(userService.isVipUser(1L)).thenReturn(false);
        when(userService.upgradeVip(1L)).thenThrow(new RuntimeException("error"));

        Result<String> result = userController.upgradeVip();
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("请稍后重试"));
    }

    @Test
    @DisplayName("logout - 带token退出")
    void logout_WithToken() {
        request.addHeader("Authorization", "Bearer test-token-123");
        when(redisUtil.delete("user:session:test-token-123")).thenReturn(true);

        Result<String> result = userController.logout(request);
        assertEquals(200, result.getCode());
        assertEquals("退出登录成功", result.getData());
        verify(redisUtil).delete("user:session:test-token-123");
    }

    @Test
    @DisplayName("logout - 无token")
    void logout_NoToken() {
        Result<String> result = userController.logout(request);
        assertEquals(200, result.getCode());
        assertEquals("退出登录成功", result.getData());
        verify(redisUtil, never()).delete(anyString());
    }

    @Test
    @DisplayName("logout - token不是Bearer开头")
    void logout_NonBearerToken() {
        request.addHeader("Authorization", "Basic some-token");

        Result<String> result = userController.logout(request);
        assertEquals(200, result.getCode());
        verify(redisUtil, never()).delete(anyString());
    }

    @Test
    @DisplayName("logout - 异常")
    void logout_Exception() {
        request.addHeader("Authorization", "Bearer test-token");
        when(redisUtil.delete(anyString())).thenThrow(new RuntimeException("redis error"));

        Result<String> result = userController.logout(request);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("退出登录失败"));
    }

    @Test
    @DisplayName("checkVipStatus - 是VIP")
    void checkVipStatus_Vip() {
        when(userService.isVipUser(1L)).thenReturn(true);

        Result<Boolean> result = userController.checkVipStatus();
        assertEquals(200, result.getCode());
        assertTrue(result.getData());
    }

    @Test
    @DisplayName("checkVipStatus - 不是VIP")
    void checkVipStatus_NotVip() {
        when(userService.isVipUser(1L)).thenReturn(false);

        Result<Boolean> result = userController.checkVipStatus();
        assertEquals(200, result.getCode());
        assertFalse(result.getData());
    }

    @Test
    @DisplayName("checkVipStatus - 未登录")
    void checkVipStatus_Unauthorized() {
        when(authentication.getPrincipal()).thenReturn(null);

        Result<Boolean> result = userController.checkVipStatus();
        assertEquals(401, result.getCode());
    }

    @Test
    @DisplayName("checkVipStatus - 异常")
    void checkVipStatus_Exception() {
        when(userService.isVipUser(1L)).thenThrow(new RuntimeException("error"));

        Result<Boolean> result = userController.checkVipStatus();
        assertEquals(500, result.getCode());
    }
}
