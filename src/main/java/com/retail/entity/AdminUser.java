package com.retail.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 管理员用户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("admin_users")
@Schema(description = "管理员用户")
public class AdminUser {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "管理员ID", example = "1")
    private Long id;

    @TableField("username")
    @Schema(description = "用户名", example = "admin")
    private String username;

    @TableField("password")
    @Schema(description = "密码", example = "加密后的密码")
    private String password;

    @TableField("real_name")
    @Schema(description = "真实姓名", example = "管理员")
    private String realName;

    @TableField("email")
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @TableField("phone")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @TableField("role")
    @Schema(description = "角色", example = "ADMIN")
    private String role;

    @TableField("status")
    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @TableField("last_login_time")
    @Schema(description = "最后登录时间", example = "2024-01-01T10:00:00")
    private LocalDateTime lastLoginTime;

    @TableField("last_login_ip")
    @Schema(description = "最后登录IP", example = "192.168.1.1")
    private String lastLoginIp;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @TableLogic
    @TableField("deleted")
    @Schema(description = "是否删除：0-未删除，1-已删除", example = "0")
    private Integer deleted;
}
