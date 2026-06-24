package com.retail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.dto.UserSearchDTO;
import com.retail.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理员用户数据访问层
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<User> {

    /**
     * 分页查询用户列表
     * @param page 分页对象
     * @param keyword 搜索关键词
     * @param role 角色筛选
     * @param status 状态筛选
     * @return 用户分页列表
     */
    IPage<User> selectUserListWithPage(Page<User> page, 
                                      @Param("keyword") String keyword,
                                      @Param("role") String role,
                                      @Param("status") Integer status);

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     * @return 删除数量
     */
    int batchDeleteUsers(@Param("ids") List<Long> ids);
    
    /**
     * 批量更新用户状态
     * @param ids 用户ID列表
     * @param status 用户状态
     * @return 更新数量
     */
    int updateUserStatus(@Param("ids") List<Long> ids, @Param("status") Integer status);

    /**
     * 统计用户总数
     * @return 用户总数
     */
    Long countTotalUsers();

    /**
     * 统计活跃用户数
     * @return 活跃用户数
     */
    Long countActiveUsers();

    /**
     * 统计今日新增用户数
     * @return 今日新增用户数
     */
    Long countTodayNewUsers();

    /**
     * 按角色统计用户数
     * @return 角色统计结果
     */
    List<java.util.Map<String, Object>> countUsersByRole();

    /**
     * 按状态统计用户数
     * @return 状态统计结果
     */
    List<java.util.Map<String, Object>> countUsersByStatus();

    /**
     * 分页查询用户列表
     */
    List<User> selectUserList(@Param("searchDTO") UserSearchDTO searchDTO);

    /**
     * 更新用户状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 更新用户角色
     */
    int updateRole(@Param("id") Long id, @Param("role") Integer role);

    /**
     * 更新用户密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * 检查邮箱是否存在
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * 快速搜索用户
     */
    List<User> quickSearchUsers(@Param("keyword") String keyword, @Param("limit") Integer limit);

    /**
     * 统计管理员用户数
     */
    long countAdminUsers();

    /**
     * 统计VIP用户数
     */
    long countVipUsers();
}
