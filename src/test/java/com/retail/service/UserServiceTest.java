package com.retail.service;

import com.retail.entity.User;
import com.retail.repository.UserRepository;
import com.retail.service.impl.UserServiceImpl;
import com.retail.util.JwtUtil;
import com.retail.util.RedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPass");
        user.setPhone("13800138000");
        user.setEmail("test@example.com");
        user.setUserType(0);
        user.setStatus(1);

        ReflectionTestUtils.setField(userService, "userSessionTime", 24);
    }

    @Nested
    @DisplayName("用户注册")
    class Register {

        @Test
        @DisplayName("注册成功")
        void register_Success() {
            when(userRepository.findByUsername("testuser")).thenReturn(null);
            when(userRepository.findByPhone("13800138000")).thenReturn(null);
            when(passwordEncoder.encode("password")).thenReturn("encodedPass");
            doReturn(true).when(userService).save(any(User.class));

            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setPassword("password");
            newUser.setPhone("13800138000");

            boolean result = userService.register(newUser);
            assertTrue(result);
        }

        @Test
        @DisplayName("注册失败 - 用户名已存在")
        void register_UsernameExists() {
            when(userRepository.findByUsername("testuser")).thenReturn(user);
            User newUser = new User();
            newUser.setUsername("testuser");
            newUser.setPassword("password");
            newUser.setPhone("13800138000");

            boolean result = userService.register(newUser);
            assertFalse(result);
        }

        @Test
        @DisplayName("注册失败 - 手机号已存在")
        void register_PhoneExists() {
            when(userRepository.findByUsername("newuser")).thenReturn(null);
            when(userRepository.findByPhone("13800138000")).thenReturn(user);
            User newUser = new User();
            newUser.setUsername("newuser");
            newUser.setPassword("password");
            newUser.setPhone("13800138000");

            boolean result = userService.register(newUser);
            assertFalse(result);
        }

        @Test
        @DisplayName("注册失败 - 异常处理")
        void register_Exception() {
            when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("DB error"));
            User newUser = new User();
            newUser.setUsername("testuser");
            boolean result = userService.register(newUser);
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("用户登录")
    class Login {

        @Test
        @DisplayName("登录成功")
        void login_Success() {
            when(userRepository.findByUsername("testuser")).thenReturn(user);
            when(passwordEncoder.matches("password", "encodedPass")).thenReturn(true);
            when(jwtUtil.generateToken(1L, "testuser", 0)).thenReturn("jwt-token-123");

            String token = userService.login("testuser", "password");
            assertNotNull(token);
            assertEquals("jwt-token-123", token);
            verify(redisUtil).set(eq("user:session:jwt-token-123"), any(), anyLong(), any());
            verify(redisUtil).set(eq("user:id:1"), eq("jwt-token-123"), anyLong(), any());
        }

        @Test
        @DisplayName("登录失败 - 用户不存在")
        void login_UserNotFound() {
            when(userRepository.findByUsername("nobody")).thenReturn(null);
            String token = userService.login("nobody", "password");
            assertNull(token);
        }

        @Test
        @DisplayName("登录失败 - 密码错误")
        void login_WrongPassword() {
            when(userRepository.findByUsername("testuser")).thenReturn(user);
            when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);
            String token = userService.login("testuser", "wrong");
            assertNull(token);
        }

        @Test
        @DisplayName("登录失败 - 用户被禁用")
        void login_Disabled() {
            user.setStatus(0);
            when(userRepository.findByUsername("testuser")).thenReturn(user);
            String token = userService.login("testuser", "password");
            assertNull(token);
        }

        @Test
        @DisplayName("登录失败 - 异常处理")
        void login_Exception() {
            when(userRepository.findByUsername(anyString())).thenThrow(new RuntimeException("DB error"));
            String token = userService.login("testuser", "password");
            assertNull(token);
        }
    }

    @Nested
    @DisplayName("用户查询")
    class UserQuery {

        @Test
        @DisplayName("getByUsername")
        void getByUsername() {
            when(userRepository.findByUsername("testuser")).thenReturn(user);
            User result = userService.getByUsername("testuser");
            assertNotNull(result);
        }

        @Test
        @DisplayName("getByPhone")
        void getByPhone() {
            when(userRepository.findByPhone("13800138000")).thenReturn(user);
            User result = userService.getByPhone("13800138000");
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("VIP管理")
    class VipManagement {

        @Test
        @DisplayName("upgradeVip - 升级成功")
        void upgradeVip_Success() {
            doReturn(user).when(userService).getById(1L);
            doReturn(true).when(userService).updateById(any(User.class));
            when(redisUtil.get("user:id:1")).thenReturn("token123");

            boolean result = userService.upgradeVip(1L);
            assertTrue(result);
            assertEquals(1, user.getUserType());
            assertNotNull(user.getVipExpireTime());
        }

        @Test
        @DisplayName("upgradeVip - 用户不存在")
        void upgradeVip_UserNotFound() {
            doReturn(null).when(userService).getById(999L);
            boolean result = userService.upgradeVip(999L);
            assertFalse(result);
        }

        @Test
        @DisplayName("isVipUser - 是VIP")
        void isVipUser_True() {
            user.setUserType(1);
            user.setVipExpireTime(LocalDateTime.now().plusDays(30));
            doReturn(user).when(userService).getById(1L);
            assertTrue(userService.isVipUser(1L));
        }

        @Test
        @DisplayName("isVipUser - 非VIP")
        void isVipUser_False() {
            doReturn(user).when(userService).getById(1L);
            assertFalse(userService.isVipUser(1L));
        }

        @Test
        @DisplayName("isVipUser - VIP已过期")
        void isVipUser_Expired() {
            user.setUserType(1);
            user.setVipExpireTime(LocalDateTime.now().minusDays(1));
            doReturn(user).when(userService).getById(1L);
            assertFalse(userService.isVipUser(1L));
        }

        @Test
        @DisplayName("isVipUser - 异常处理")
        void isVipUser_Exception() {
            doThrow(new RuntimeException("DB error")).when(userService).getById(1L);
            assertFalse(userService.isVipUser(1L));
        }
    }

    @Nested
    @DisplayName("用户信息更新")
    class UpdateUserInfo {

        @Test
        @DisplayName("updateUserInfo - 成功")
        void updateUserInfo_Success() {
            doReturn(true).when(userService).updateById(any(User.class));
            when(redisUtil.get("user:id:1")).thenReturn("token123");

            boolean result = userService.updateUserInfo(user);
            assertTrue(result);
            verify(redisUtil).set(eq("user:session:token123"), any(), anyLong(), any());
        }

        @Test
        @DisplayName("updateUserInfo - 异常处理")
        void updateUserInfo_Exception() {
            doThrow(new RuntimeException("DB error")).when(userService).updateById(any(User.class));
            boolean result = userService.updateUserInfo(user);
            assertFalse(result);
        }
    }
}
