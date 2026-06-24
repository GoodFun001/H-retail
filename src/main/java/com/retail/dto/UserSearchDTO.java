package com.retail.dto;

import lombok.Data;

/**
 * 用户搜索DTO
 */
@Data
public class UserSearchDTO {

    private String keyword;
    
    private String username;

    private String email;

    private String phone;

    private Integer role;

    private Integer status;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
