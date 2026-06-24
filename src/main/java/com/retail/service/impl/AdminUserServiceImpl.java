package com.retail.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.retail.dto.UserCreateDTO;
import com.retail.dto.UserDTO;
import com.retail.dto.UserSearchDTO;
import com.retail.dto.UserUpdateDTO;
import com.retail.entity.User;
import com.retail.repository.AdminUserRepository;
import com.retail.service.AdminUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理员用户服务实现类
 * 【重构说明】引入Repository层，Service通过AdminUserRepository访问数据，
 * 不再直接依赖AdminUserMapper，符合分层架构原则。
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public PageInfo<UserDTO> getUserList(UserSearchDTO searchDTO) {
        // 设置分页
        PageHelper.startPage(searchDTO.getPageNum(), searchDTO.getPageSize());
        
        // 查询数据
        List<User> users = adminUserRepository.selectUserList(searchDTO);
        
        // 转换为DTO
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageInfo<>(userDTOs);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = adminUserRepository.selectById(id);
        return user != null ? convertToDTO(user) : null;
    }

    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO createDTO) {
        // 检查用户名是否存在
        if (existsByUsername(createDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查邮箱是否存在
        if (existsByEmail(createDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 创建用户
        User user = new User();
        BeanUtils.copyProperties(createDTO, user);
        user.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        user.setStatus(createDTO.getStatus() != null ? createDTO.getStatus() : 1);
        
        adminUserRepository.insert(user);
        
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO updateDTO) {
        User user = adminUserRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果更新用户名，检查是否存在
        if (updateDTO.getUsername() != null && 
            !updateDTO.getUsername().equals(user.getUsername()) &&
            existsByUsername(updateDTO.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 如果更新邮箱，检查是否存在
        if (updateDTO.getEmail() != null && 
            !updateDTO.getEmail().equals(user.getEmail()) &&
            existsByEmail(updateDTO.getEmail())) {
            throw new RuntimeException("邮箱已存在");
        }
        
        // 更新用户信息
        BeanUtils.copyProperties(updateDTO, user, "id", "password", "createTime");
        
        adminUserRepository.updateById(user);
        
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {
        User user = adminUserRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 防止删除管理员
        if ("admin".equals(user.getUsername())) {
            throw new RuntimeException("不能删除管理员用户");
        }
        
        return adminUserRepository.deleteById(id) > 0;
    }

    @Override
    @Transactional
    public int batchDeleteUsers(List<Long> ids) {
        int count = 0;
        for (Long id : ids) {
            User user = adminUserRepository.selectById(id);
            if (user != null && !"admin".equals(user.getUsername())) {
                if (adminUserRepository.deleteById(id) > 0) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    @Transactional
    public boolean updateUserStatus(Long id, Integer status) {
        User user = adminUserRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 防止禁用管理员
        if ("admin".equals(user.getUsername()) && status == 0) {
            throw new RuntimeException("不能禁用管理员用户");
        }
        
        return adminUserRepository.updateStatus(id, status) > 0;
    }
    
    @Override
    @Transactional
    public int batchUpdateUserStatus(List<Long> ids, Integer status) {
        // 检查是否包含管理员用户，防止禁用管理员
        for (Long id : ids) {
            User user = adminUserRepository.selectById(id);
            if (user != null && "admin".equals(user.getUsername()) && status == 0) {
                throw new RuntimeException("不能禁用管理员用户");
            }
        }
        
        // 批量更新用户状态
        return adminUserRepository.updateUserStatus(ids, status);
    }

    @Override
    @Transactional
    public boolean updateUserRole(Long id, Integer role) {
        User user = adminUserRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 防止修改管理员角色
        if ("admin".equals(user.getUsername())) {
            throw new RuntimeException("不能修改管理员角色");
        }
        
        return adminUserRepository.updateRole(id, role) > 0;
    }

    @Override
    @Transactional
    public boolean resetPassword(Long id, String newPassword) {
        User user = adminUserRepository.selectById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        return adminUserRepository.updatePassword(id, encodedPassword) > 0;
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminUserRepository.existsByEmail(email);
    }

    @Override
    public Page<UserDTO> searchUsers(String keyword, Integer role, Integer status, Pageable pageable) {
        // 使用PageHelper进行分页
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 构建搜索条件
        UserSearchDTO searchDTO = new UserSearchDTO();
        searchDTO.setUsername(keyword);
        searchDTO.setRole(role);
        searchDTO.setStatus(status);
        
        // 查询数据
        List<User> users = adminUserRepository.selectUserList(searchDTO);
        
        // 转换为DTO
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 转换为Spring Data的Page对象
        PageInfo<UserDTO> pageInfo = new PageInfo<>(userDTOs);
        return new PageImpl<>(pageInfo.getList(), pageable, pageInfo.getTotal());
    }

    @Override
    public Object getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 查询总用户数
        long totalUsers = adminUserRepository.countTotalUsers();
        
        // 查询活跃用户数
        long activeUsers = adminUserRepository.countActiveUsers();
        
        // 查询管理员用户数
        long adminUsers = adminUserRepository.countAdminUsers();
        
        // 查询VIP用户数
        long vipUsers = adminUserRepository.countVipUsers();
        
        statistics.put("totalUsers", totalUsers);
        statistics.put("activeUsers", activeUsers);
        statistics.put("adminUsers", adminUsers);
        statistics.put("vipUsers", vipUsers);
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> quickSearchUsers(String keyword, Integer limit) {
        // 查询用户
        List<User> users = adminUserRepository.quickSearchUsers(keyword, limit);
        
        // 转换为简化的Map格式
        List<Map<String, Object>> result = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("role", user.getUserType());
            result.add(userMap);
        }
        
        return result;
    }
    
    @Override
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        // 使用PageHelper进行分页
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());
        
        // 查询所有用户（没有过滤条件）
        List<User> users = adminUserRepository.selectList(null);
        
        // 转换为DTO
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 转换为Spring Data的Page对象
        PageInfo<UserDTO> pageInfo = new PageInfo<>(userDTOs);
        return new PageImpl<>(pageInfo.getList(), pageable, pageInfo.getTotal());
    }

    /**
     * 实体转DTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        // 注意：前端使用role字段，后端使用userType字段
        // 0-普通用户，1-VIP用户，2-管理员
        if (user.getUserType() != null) {
            switch (user.getUserType()) {
                case 0: 
                    dto.setRole("user");
                    break;
                case 1: 
                    dto.setRole("vip");
                    break;
                case 2: 
                    dto.setRole("admin");
                    break;
                default: 
                    dto.setRole("user");
            }
        } else {
            dto.setRole("user");
        }
        return dto;
    }
}