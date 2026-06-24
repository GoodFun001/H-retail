package com.retail.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.dto.UserSearchDTO;
import com.retail.entity.User;
import com.retail.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户Repository层
 * 封装AdminUserMapper的数据访问操作，提供业务语义化的数据查询方法
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class AdminUserRepository {

    private final AdminUserMapper adminUserMapper;

    /**
     * 根据ID查询用户
     */
    public User findById(Long id) {
        return adminUserMapper.selectById(id);
    }

    /**
     * 根据ID查询用户（别名，兼容旧代码）
     */
    public User selectById(Long id) {
        return findById(id);
    }

    /**
     * 保存用户
     */
    public int insert(User user) {
        return adminUserMapper.insert(user);
    }

    /**
     * 更新用户
     */
    public int update(User user) {
        return adminUserMapper.updateById(user);
    }

    /**
     * 更新用户（别名，兼容旧代码）
     */
    public int updateById(User user) {
        return adminUserMapper.updateById(user);
    }

    /**
     * 根据ID删除用户
     */
    public int deleteById(Long id) {
        return adminUserMapper.deleteById(id);
    }

    /**
     * 分页查询用户列表
     */
    public IPage<User> selectPage(Page<User> page, LambdaQueryWrapper<User> wrapper) {
        return adminUserMapper.selectPage(page, wrapper);
    }

    /**
     * 条件查询用户列表
     */
    public List<User> selectList(LambdaQueryWrapper<User> wrapper) {
        return adminUserMapper.selectList(wrapper);
    }

    /**
     * 分页查询用户列表（使用自定义SQL）
     */
    public IPage<User> selectUserListWithPage(Page<User> page, String keyword, String role, Integer status) {
        return adminUserMapper.selectUserListWithPage(page, keyword, role, status);
    }

    /**
     * 按条件查询用户列表（DTO方式）
     */
    public List<User> selectUserList(UserSearchDTO searchDTO) {
        return adminUserMapper.selectUserList(searchDTO);
    }

    /**
     * 批量删除用户
     */
    public int batchDeleteUsers(List<Long> ids) {
        return adminUserMapper.batchDeleteUsers(ids);
    }

    /**
     * 批量更新用户状态
     */
    public int updateUserStatus(List<Long> ids, Integer status) {
        return adminUserMapper.updateUserStatus(ids, status);
    }

    /**
     * 统计用户总数
     */
    public Long countTotalUsers() {
        return adminUserMapper.countTotalUsers();
    }

    /**
     * 统计活跃用户数
     */
    public Long countActiveUsers() {
        return adminUserMapper.countActiveUsers();
    }

    /**
     * 统计今日新增用户数
     */
    public Long countTodayNewUsers() {
        return adminUserMapper.countTodayNewUsers();
    }

    /**
     * 按角色统计用户数
     */
    public List<Map<String, Object>> countUsersByRole() {
        return adminUserMapper.countUsersByRole();
    }

    /**
     * 按状态统计用户数
     */
    public List<Map<String, Object>> countUsersByStatus() {
        return adminUserMapper.countUsersByStatus();
    }

    /**
     * 更新用户状态
     */
    public int updateStatus(Long id, Integer status) {
        return adminUserMapper.updateStatus(id, status);
    }

    /**
     * 更新用户角色
     */
    public int updateRole(Long id, Integer role) {
        return adminUserMapper.updateRole(id, role);
    }

    /**
     * 更新用户密码
     */
    public int updatePassword(Long id, String password) {
        return adminUserMapper.updatePassword(id, password);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return adminUserMapper.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     */
    public boolean existsByEmail(String email) {
        return adminUserMapper.existsByEmail(email);
    }

    /**
     * 快速搜索用户
     */
    public List<User> quickSearchUsers(String keyword, Integer limit) {
        return adminUserMapper.quickSearchUsers(keyword, limit);
    }

    /**
     * 统计管理员用户数
     */
    public long countAdminUsers() {
        return adminUserMapper.countAdminUsers();
    }

    /**
     * 统计VIP用户数
     */
    public long countVipUsers() {
        return adminUserMapper.countVipUsers();
    }

    /**
     * 查询所有用户（无条件）
     */
    public List<User> findAll() {
        return adminUserMapper.selectList(null);
    }
}
