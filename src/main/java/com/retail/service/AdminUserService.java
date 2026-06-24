package com.retail.service;

import com.retail.dto.UserCreateDTO;
import com.retail.dto.UserDTO;
import com.retail.dto.UserSearchDTO;
import com.retail.dto.UserUpdateDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService {
    
    /**
     * 分页查询用户列表
     */
    PageInfo<UserDTO> getUserList(UserSearchDTO searchDTO);
    
    /**
     * 获取所有用户列表（分页）
     */
    Page<UserDTO> getAllUsers(Pageable pageable);
    
    /**
     * 根据ID获取用户详情
     */
    UserDTO getUserById(Long id);
    
    /**
     * 创建用户
     */
    UserDTO createUser(UserCreateDTO createDTO);
    
    /**
     * 更新用户信息
     */
    UserDTO updateUser(Long id, UserUpdateDTO updateDTO);
    
    /**
     * 删除用户
     */
    boolean deleteUser(Long id);
    
    /**
     * 批量删除用户
     */
    int batchDeleteUsers(List<Long> ids);
    
    /**
     * 启用/禁用用户
     */
    boolean updateUserStatus(Long id, Integer status);
    
    /**
     * 批量更新用户状态
     */
    int batchUpdateUserStatus(List<Long> ids, Integer status);
    
    /**
     * 更新用户角色
     */
    boolean updateUserRole(Long id, Integer role);
    
    /**
     * 重置用户密码
     */
    boolean resetPassword(Long id, String newPassword);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(String email);
    
    /**
     * 搜索用户
     */
    Page<UserDTO> searchUsers(String keyword, Integer role, Integer status, Pageable pageable);
    
    /**
     * 获取用户统计信息
     */
    Object getUserStatistics();
    
    /**
     * 快速搜索用户
     */
    List<Map<String, Object>> quickSearchUsers(String keyword, Integer limit);
}