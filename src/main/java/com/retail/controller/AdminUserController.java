package com.retail.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.retail.common.Result;
import com.retail.entity.User;
import com.retail.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理控制器
 */
@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员用户管理", description = "管理员对用户进行管理的相关接口")
public class AdminUserController {

    private final UserService userService;

    /**
     * 获取用户列表（分页）
     */
    @GetMapping
    @Operation(summary = "获取用户列表", description = "管理员获取用户列表，支持分页和搜索")
    public Result<IPage<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchKey) {
        try {
            Page<User> page = new Page<>(pageNum, pageSize);
            IPage<User> userList = userService.page(page);
            return Result.success(userList);
        } catch (Exception e) {
            log.error("获取用户列表失败: {}", e.getMessage(), e);
            return Result.error("获取用户列表失败");
        }
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取用户详情", description = "管理员根据用户ID获取用户详细信息")
    public Result<User> getUserDetail(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        try {
            User user = userService.getById(id);
            if (user != null) {
                // 清除敏感信息
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            log.error("获取用户详情失败: {}", e.getMessage(), e);
            return Result.error("获取用户详情失败");
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "管理员更新用户信息")
    public Result<String> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Valid @RequestBody User user) {
        try {
            // 设置用户ID，防止修改其他用户信息
            user.setId(id);
            boolean success = userService.updateUserInfo(user);
            if (success) {
                return Result.success("更新用户信息成功");
            } else {
                return Result.error("更新用户信息失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败: {}", e.getMessage(), e);
            return Result.error("更新用户信息失败");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "管理员删除用户")
    public Result<String> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        try {
            boolean success = userService.removeById(id);
            if (success) {
                return Result.success("删除用户成功");
            } else {
                return Result.error("删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败: {}", e.getMessage(), e);
            return Result.error("删除用户失败");
        }
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "管理员更新用户状态（启用/禁用）")
    public Result<String> updateUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @RequestParam Integer status) {
        try {
            User user = new User();
            user.setId(id);
            user.setStatus(status);
            boolean success = userService.updateById(user);
            if (success) {
                return Result.success("更新用户状态成功");
            } else {
                return Result.error("更新用户状态失败");
            }
        } catch (Exception e) {
            log.error("更新用户状态失败: {}", e.getMessage(), e);
            return Result.error("更新用户状态失败");
        }
    }

    /**
     * 批量更新用户状态
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新用户状态", description = "管理员批量更新用户状态")
    public Result<String> batchUpdateUserStatus(@RequestBody List<Map<String, Object>> userStatusList) {
        try {
            for (Map<String, Object> userStatus : userStatusList) {
                Long id = Long.valueOf(userStatus.get("id").toString());
                Integer status = Integer.valueOf(userStatus.get("status").toString());
                User user = new User();
                user.setId(id);
                user.setStatus(status);
                userService.updateById(user);
            }
            return Result.success("批量更新用户状态成功");
        } catch (Exception e) {
            log.error("批量更新用户状态失败: {}", e.getMessage(), e);
            return Result.error("批量更新用户状态失败");
        }
    }
    
    /**
     * 更新用户VIP状态
     */
    @PutMapping("/{id}/vip")
    @Operation(summary = "更新用户VIP状态", description = "管理员更新用户VIP状态（普通用户/VIP用户）")
    public Result<String> updateUserVipStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @RequestParam Integer userType) {
        try {
            User user = new User();
            user.setId(id);
            user.setUserType(userType);
            boolean success = userService.updateById(user);
            if (success) {
                return Result.success("更新用户VIP状态成功");
            } else {
                return Result.error("更新用户VIP状态失败");
            }
        } catch (Exception e) {
            log.error("更新用户VIP状态失败: {}", e.getMessage(), e);
            return Result.error("更新用户VIP状态失败");
        }
    }
}