package com.retail.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GlobalExceptionHandler 全局异常处理测试")
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("处理 RuntimeException")
    void handleRuntimeException() {
        RuntimeException ex = new RuntimeException("运行时错误");
        Result<Void> result = handler.handleRuntimeException(ex);
        assertEquals(500, result.getCode());
        assertEquals("运行时错误", result.getMsg());
    }

    @Test
    @DisplayName("处理 IllegalArgumentException")
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("参数不合法");
        Result<Void> result = handler.handleIllegalArgumentException(ex);
        assertEquals(400, result.getCode());
        assertEquals("参数不合法", result.getMsg());
    }

    @Test
    @DisplayName("处理通用 Exception")
    void handleException() {
        Exception ex = new Exception("未知错误");
        Result<Void> result = handler.handleException(ex);
        assertEquals(500, result.getCode());
        assertEquals("系统内部错误", result.getMsg());
    }
}
