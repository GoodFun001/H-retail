package com.retail.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.common.Result;
import com.retail.entity.User;
import com.retail.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUserController 测试")
class AdminUserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminUserController controller;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("secret");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setStatus(1);
        testUser.setUserType(0);
    }

    @Test
    @DisplayName("getUserList - 正常分页返回")
    void getUserList_Success() {
        Page<User> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(testUser));
        page.setTotal(1);
        when(userService.page(any(Page.class))).thenReturn(page);

        Result<IPage<User>> result = controller.getUserList(1, 10, null);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getTotal());
    }

    @Test
    @DisplayName("getUserList - 异常")
    void getUserList_Exception() {
        when(userService.page(any(Page.class))).thenThrow(new RuntimeException("DB error"));

        Result<IPage<User>> result = controller.getUserList(1, 10, null);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("获取用户列表失败"));
    }

    @Test
    @DisplayName("getUserDetail - 用户存在")
    void getUserDetail_Exists() {
        when(userService.getById(1L)).thenReturn(testUser);

        Result<User> result = controller.getUserDetail(1L);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertNull(result.getData().getPassword());
    }

    @Test
    @DisplayName("getUserDetail - 用户不存在")
    void getUserDetail_NotFound() {
        when(userService.getById(999L)).thenReturn(null);

        Result<User> result = controller.getUserDetail(999L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("用户不存在"));
    }

    @Test
    @DisplayName("getUserDetail - 异常")
    void getUserDetail_Exception() {
        when(userService.getById(1L)).thenThrow(new RuntimeException("DB error"));

        Result<User> result = controller.getUserDetail(1L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("获取用户详情失败"));
    }

    @Test
    @DisplayName("updateUser - 成功")
    void updateUser_Success() {
        User updateUser = new User();
        updateUser.setUsername("updated");
        when(userService.updateUserInfo(any(User.class))).thenReturn(true);

        Result<String> result = controller.updateUser(1L, updateUser);
        assertEquals(200, result.getCode());
        assertEquals("更新用户信息成功", result.getData());
    }

    @Test
    @DisplayName("updateUser - 失败")
    void updateUser_Failed() {
        when(userService.updateUserInfo(any(User.class))).thenReturn(false);

        Result<String> result = controller.updateUser(1L, new User());
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新用户信息失败"));
    }

    @Test
    @DisplayName("updateUser - 异常")
    void updateUser_Exception() {
        when(userService.updateUserInfo(any(User.class))).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.updateUser(1L, new User());
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新用户信息失败"));
    }

    @Test
    @DisplayName("deleteUser - 成功")
    void deleteUser_Success() {
        when(userService.removeById(1L)).thenReturn(true);

        Result<String> result = controller.deleteUser(1L);
        assertEquals(200, result.getCode());
        assertEquals("删除用户成功", result.getData());
    }

    @Test
    @DisplayName("deleteUser - 失败")
    void deleteUser_Failed() {
        when(userService.removeById(1L)).thenReturn(false);

        Result<String> result = controller.deleteUser(1L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("删除用户失败"));
    }

    @Test
    @DisplayName("deleteUser - 异常")
    void deleteUser_Exception() {
        when(userService.removeById(1L)).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.deleteUser(1L);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("删除用户失败"));
    }

    @Test
    @DisplayName("updateUserStatus - 成功")
    void updateUserStatus_Success() {
        when(userService.updateById(any(User.class))).thenReturn(true);

        Result<String> result = controller.updateUserStatus(1L, 0);
        assertEquals(200, result.getCode());
        assertEquals("更新用户状态成功", result.getData());
    }

    @Test
    @DisplayName("updateUserStatus - 失败")
    void updateUserStatus_Failed() {
        when(userService.updateById(any(User.class))).thenReturn(false);

        Result<String> result = controller.updateUserStatus(1L, 0);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新用户状态失败"));
    }

    @Test
    @DisplayName("updateUserStatus - 异常")
    void updateUserStatus_Exception() {
        when(userService.updateById(any(User.class))).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.updateUserStatus(1L, 0);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新用户状态失败"));
    }

    @Test
    @DisplayName("batchUpdateUserStatus - 成功")
    void batchUpdateUserStatus_Success() {
        when(userService.updateById(any(User.class))).thenReturn(true);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("id", 1);
        item1.put("status", 0);
        list.add(item1);
        Map<String, Object> item2 = new HashMap<>();
        item2.put("id", 2);
        item2.put("status", 1);
        list.add(item2);

        Result<String> result = controller.batchUpdateUserStatus(list);
        assertEquals(200, result.getCode());
        assertEquals("批量更新用户状态成功", result.getData());
    }

    @Test
    @DisplayName("batchUpdateUserStatus - 异常")
    void batchUpdateUserStatus_Exception() {
        when(userService.updateById(any(User.class))).thenThrow(new RuntimeException("error"));

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("id", 1);
        item.put("status", 0);
        list.add(item);

        Result<String> result = controller.batchUpdateUserStatus(list);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("批量更新用户状态失败"));
    }

    @Test
    @DisplayName("updateUserVipStatus - 成功")
    void updateUserVipStatus_Success() {
        when(userService.updateById(any(User.class))).thenReturn(true);

        Result<String> result = controller.updateUserVipStatus(1L, 1);
        assertEquals(200, result.getCode());
        assertEquals("更新用户VIP状态成功", result.getData());
    }

    @Test
    @DisplayName("updateUserVipStatus - 失败")
    void updateUserVipStatus_Failed() {
        when(userService.updateById(any(User.class))).thenReturn(false);

        Result<String> result = controller.updateUserVipStatus(1L, 1);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新用户VIP状态失败"));
    }

    @Test
    @DisplayName("updateUserVipStatus - 异常")
    void updateUserVipStatus_Exception() {
        when(userService.updateById(any(User.class))).thenThrow(new RuntimeException("error"));

        Result<String> result = controller.updateUserVipStatus(1L, 1);
        assertEquals(500, result.getCode());
        assertTrue(result.getMsg().contains("更新用户VIP状态失败"));
    }
}
