package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.retail.entity.User;
import com.retail.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRepository 测试")
class UserRepositoryTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("pass");
        user.setPhone("13800138000");
        user.setStatus(1);
        user.setDeleted(0);
    }

    @Test
    @DisplayName("findById - 查询用户")
    void findById() {
        when(userMapper.selectById(1L)).thenReturn(user);
        User result = userRepository.findById(1L);
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    @DisplayName("selectById - 别名方法")
    void selectById() {
        when(userMapper.selectById(1L)).thenReturn(user);
        User result = userRepository.selectById(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("findByUsername - 按用户名查询")
    void findByUsername() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        User result = userRepository.findByUsername("testuser");
        assertNotNull(result);
    }

    @Test
    @DisplayName("findByUsername - 用户不存在")
    void findByUsername_NotFound() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        User result = userRepository.findByUsername("nobody");
        assertNull(result);
    }

    @Test
    @DisplayName("findByPhone - 按手机号查询")
    void findByPhone() {
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        User result = userRepository.findByPhone("13800138000");
        assertNotNull(result);
    }

    @Test
    @DisplayName("save - 保存用户")
    void save() {
        when(userMapper.insert(user)).thenReturn(1);
        assertTrue(userRepository.save(user));
    }

    @Test
    @DisplayName("insert - 别名保存")
    void insert() {
        when(userMapper.insert(user)).thenReturn(1);
        assertEquals(1, userRepository.insert(user));
    }

    @Test
    @DisplayName("update - 更新用户")
    void update() {
        when(userMapper.updateById(user)).thenReturn(1);
        assertTrue(userRepository.update(user));
    }

    @Test
    @DisplayName("updateById - 别名更新")
    void updateById() {
        when(userMapper.updateById(user)).thenReturn(1);
        assertEquals(1, userRepository.updateById(user));
    }

    @Test
    @DisplayName("deleteById - 删除用户")
    void deleteById() {
        when(userMapper.deleteById(1L)).thenReturn(1);
        assertTrue(userRepository.deleteById(1L));
    }

    @Test
    @DisplayName("findAll - 查询所有用户")
    void findAll() {
        when(userMapper.selectList(null)).thenReturn(Arrays.asList(user));
        List<User> list = userRepository.findAll();
        assertEquals(1, list.size());
    }

    @Test
    @DisplayName("updateBatchById - 批量更新")
    void updateBatchById() {
        when(userMapper.updateById(any(User.class))).thenReturn(1);
        assertTrue(userRepository.updateBatchById(Arrays.asList(user, user)));
    }
}
