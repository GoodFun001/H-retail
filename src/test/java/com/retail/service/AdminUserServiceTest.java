package com.retail.service;

import com.retail.dto.UserCreateDTO;
import com.retail.dto.UserDTO;
import com.retail.dto.UserUpdateDTO;
import com.retail.entity.User;
import com.retail.repository.AdminUserRepository;
import com.retail.service.impl.AdminUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserService 测试")
class AdminUserServiceTest {

    @Mock
    private AdminUserRepository adminUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminUserServiceImpl adminUserService;

    private User user;
    private Pageable pageable;

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
        pageable = PageRequest.of(0, 10);
    }

    @Nested
    @DisplayName("用户CRUD")
    class UserCRUD {

        @Test
        @DisplayName("getUserById - 存在")
        void getUserById_Exists() {
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            UserDTO result = adminUserService.getUserById(1L);
            assertNotNull(result);
            assertEquals("testuser", result.getUsername());
        }

        @Test
        @DisplayName("getUserById - 不存在")
        void getUserById_NotFound() {
            when(adminUserRepository.selectById(999L)).thenReturn(null);
            assertNull(adminUserService.getUserById(999L));
        }

        @Test
        @DisplayName("createUser - 成功")
        void createUser_Success() {
            UserCreateDTO dto = new UserCreateDTO();
            dto.setUsername("newuser");
            dto.setPassword("password");
            dto.setEmail("new@example.com");
            dto.setStatus(1);

            when(adminUserRepository.existsByUsername("newuser")).thenReturn(false);
            when(adminUserRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(passwordEncoder.encode("password")).thenReturn("encodedPass");
            when(adminUserRepository.insert(any())).thenReturn(1);

            UserDTO result = adminUserService.createUser(dto);
            assertNotNull(result);
        }

        @Test
        @DisplayName("createUser - 用户名已存在")
        void createUser_DuplicateUsername() {
            UserCreateDTO dto = new UserCreateDTO();
            dto.setUsername("testuser");
            dto.setEmail("new@example.com");

            when(adminUserRepository.existsByUsername("testuser")).thenReturn(true);
            assertThrows(RuntimeException.class, () -> adminUserService.createUser(dto));
        }

        @Test
        @DisplayName("createUser - 邮箱已存在")
        void createUser_DuplicateEmail() {
            UserCreateDTO dto = new UserCreateDTO();
            dto.setUsername("newuser");
            dto.setEmail("test@example.com");

            when(adminUserRepository.existsByUsername("newuser")).thenReturn(false);
            when(adminUserRepository.existsByEmail("test@example.com")).thenReturn(true);
            assertThrows(RuntimeException.class, () -> adminUserService.createUser(dto));
        }

        @Test
        @DisplayName("updateUser - 成功")
        void updateUser_Success() {
            UserUpdateDTO dto = new UserUpdateDTO();
            dto.setUsername("updated");

            when(adminUserRepository.selectById(1L)).thenReturn(user);
            when(adminUserRepository.updateById(any())).thenReturn(1);

            UserDTO result = adminUserService.updateUser(1L, dto);
            assertNotNull(result);
        }

        @Test
        @DisplayName("updateUser - 不存在")
        void updateUser_NotFound() {
            when(adminUserRepository.selectById(999L)).thenReturn(null);
            assertThrows(RuntimeException.class,
                    () -> adminUserService.updateUser(999L, new UserUpdateDTO()));
        }

        @Test
        @DisplayName("deleteUser - 成功")
        void deleteUser_Success() {
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            when(adminUserRepository.deleteById(1L)).thenReturn(1);
            assertTrue(adminUserService.deleteUser(1L));
        }

        @Test
        @DisplayName("deleteUser - 不能删除admin")
        void deleteUser_Admin() {
            user.setUsername("admin");
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            assertThrows(RuntimeException.class, () -> adminUserService.deleteUser(1L));
        }
    }

    @Nested
    @DisplayName("状态和角色管理")
    class StatusAndRole {

        @Test
        @DisplayName("updateUserStatus - 成功")
        void updateUserStatus_Success() {
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            when(adminUserRepository.updateStatus(1L, 0)).thenReturn(1);
            assertTrue(adminUserService.updateUserStatus(1L, 0));
        }

        @Test
        @DisplayName("updateUserStatus - 不能禁用admin")
        void updateUserStatus_Admin() {
            user.setUsername("admin");
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            assertThrows(RuntimeException.class,
                    () -> adminUserService.updateUserStatus(1L, 0));
        }

        @Test
        @DisplayName("updateUserRole - 成功")
        void updateUserRole_Success() {
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            when(adminUserRepository.updateRole(1L, 2)).thenReturn(1);
            assertTrue(adminUserService.updateUserRole(1L, 2));
        }

        @Test
        @DisplayName("updateUserRole - 不能修改admin角色")
        void updateUserRole_Admin() {
            user.setUsername("admin");
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            assertThrows(RuntimeException.class,
                    () -> adminUserService.updateUserRole(1L, 1));
        }

        @Test
        @DisplayName("resetPassword - 成功")
        void resetPassword_Success() {
            when(adminUserRepository.selectById(1L)).thenReturn(user);
            when(passwordEncoder.encode("newpass")).thenReturn("newEncoded");
            when(adminUserRepository.updatePassword(eq(1L), anyString())).thenReturn(1);
            assertTrue(adminUserService.resetPassword(1L, "newpass"));
        }
    }

    @Nested
    @DisplayName("存在性检查")
    class Existence {

        @Test
        @DisplayName("existsByUsername")
        void existsByUsername() {
            when(adminUserRepository.existsByUsername("testuser")).thenReturn(true);
            assertTrue(adminUserService.existsByUsername("testuser"));
        }

        @Test
        @DisplayName("existsByEmail")
        void existsByEmail() {
            when(adminUserRepository.existsByEmail("test@example.com")).thenReturn(true);
            assertTrue(adminUserService.existsByEmail("test@example.com"));
        }
    }

    @Nested
    @DisplayName("用户统计")
    class UserStatistics {

        @Test
        @DisplayName("getUserStatistics")
        void getUserStatistics() {
            when(adminUserRepository.countTotalUsers()).thenReturn(100L);
            when(adminUserRepository.countActiveUsers()).thenReturn(80L);
            when(adminUserRepository.countAdminUsers()).thenReturn(5L);
            when(adminUserRepository.countVipUsers()).thenReturn(20L);

            Object stats = adminUserService.getUserStatistics();
            assertNotNull(stats);
            assertTrue(stats instanceof Map);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) stats;
            assertEquals(100L, map.get("totalUsers"));
            assertEquals(80L, map.get("activeUsers"));
            assertEquals(5L, map.get("adminUsers"));
            assertEquals(20L, map.get("vipUsers"));
        }
    }

    @Nested
    @DisplayName("快速搜索")
    class QuickSearch {

        @Test
        @DisplayName("quickSearchUsers")
        void quickSearchUsers() {
            when(adminUserRepository.quickSearchUsers("test", 5)).thenReturn(Arrays.asList(user));
            List<Map<String, Object>> result = adminUserService.quickSearchUsers("test", 5);
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
}
