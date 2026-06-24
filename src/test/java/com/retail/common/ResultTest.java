package com.retail.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Result 统一响应结果测试")
class ResultTest {

    @Test
    @DisplayName("success() - 无参数")
    void success_NoArgs() {
        Result<String> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("success(T data) - 带数据")
    void success_WithData() {
        Result<String> result = Result.success("hello");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMsg());
        assertEquals("hello", result.getData());
    }

    @Test
    @DisplayName("success(String msg, T data) - 自定义消息")
    void success_WithMsgAndData() {
        Result<Integer> result = Result.success("自定义成功", 100);
        assertEquals(200, result.getCode());
        assertEquals("自定义成功", result.getMsg());
        assertEquals(100, result.getData());
    }

    @Test
    @DisplayName("error() - 无参数")
    void error_NoArgs() {
        Result<String> result = Result.error();
        assertEquals(500, result.getCode());
        assertEquals("操作失败", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("error(String msg) - 自定义消息")
    void error_WithMsg() {
        Result<String> result = Result.error("服务器错误");
        assertEquals(500, result.getCode());
        assertEquals("服务器错误", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("error(Integer code, String msg) - 自定义错误码")
    void error_WithCodeAndMsg() {
        Result<String> result = Result.error(503, "服务不可用");
        assertEquals(503, result.getCode());
        assertEquals("服务不可用", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("paramError(String msg) - 参数错误")
    void paramError() {
        Result<String> result = Result.paramError("用户名不能为空");
        assertEquals(400, result.getCode());
        assertEquals("用户名不能为空", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("unauthorized(String msg) - 未授权")
    void unauthorized() {
        Result<String> result = Result.unauthorized("请先登录");
        assertEquals(401, result.getCode());
        assertEquals("请先登录", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("forbidden(String msg) - 禁止访问")
    void forbidden() {
        Result<String> result = Result.forbidden("无权限访问");
        assertEquals(403, result.getCode());
        assertEquals("无权限访问", result.getMsg());
        assertNull(result.getData());
    }

    @Test
    @DisplayName("构造器和setter/getter")
    void constructorAndSetter() {
        Result<String> result = new Result<>();
        result.setCode(200);
        result.setMsg("test");
        result.setData("data");
        assertEquals(200, result.getCode());
        assertEquals("test", result.getMsg());
        assertEquals("data", result.getData());
    }

    @Test
    @DisplayName("全参构造器")
    void allArgsConstructor() {
        Result<String> result = new Result<>(201, "创建成功", "new-resource");
        assertEquals(201, result.getCode());
        assertEquals("创建成功", result.getMsg());
        assertEquals("new-resource", result.getData());
    }
}
