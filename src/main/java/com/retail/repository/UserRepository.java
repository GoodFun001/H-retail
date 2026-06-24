package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.entity.User;
import com.retail.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户Repository层
 * 封装UserMapper的数据访问操作，提供业务语义化的数据查询方法
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final UserMapper userMapper;

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    /**
     * 根据ID查询用户（别名，兼容旧代码）
     */
    public User selectById(Long id) {
        return findById(id);
    }

    /**
     * 根据用户名查询用户（未删除）
     */
    public User findByUsername(String username) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0)
                .last("LIMIT 1")
        );
    }

    /**
     * 根据手机号查询用户（未删除）
     */
    public User findByPhone(String phone) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .eq(User::getDeleted, 0)
                .last("LIMIT 1")
        );
    }

    /**
     * 保存用户
     */
    public boolean save(User user) {
        return userMapper.insert(user) > 0;
    }

    /**
     * 保存用户（别名，兼容旧代码）
     */
    public int insert(User user) {
        return userMapper.insert(user);
    }

    /**
     * 更新用户
     */
    public boolean update(User user) {
        return userMapper.updateById(user) > 0;
    }

    /**
     * 更新用户（别名，兼容旧代码）
     */
    public int updateById(User user) {
        return userMapper.updateById(user);
    }

    /**
     * 根据ID删除用户（逻辑删除）
     */
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }

    /**
     * 分页查询用户列表
     */
    public IPage<User> selectPage(Page<User> page) {
        return userMapper.selectPage(page, null);
    }

    /**
     * 条件查询用户列表
     */
    public List<User> selectList(LambdaQueryWrapper<User> wrapper) {
        return userMapper.selectList(wrapper);
    }

    /**
     * 查询所有用户
     */
    public List<User> findAll() {
        return userMapper.selectList(null);
    }

    /**
     * 批量更新（使用MyBatis Plus Service层能力）
     */
    public boolean updateBatchById(List<User> users) {
        for (User user : users) {
            if (userMapper.updateById(user) <= 0) return false;
        }
        return true;
    }

    /**
     * 条件更新
     */
    public int update(LambdaQueryWrapper<User> wrapper) {
        return userMapper.update(null, wrapper);
    }
}
