package com.retail.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MyMetaObjectHandler 测试")
class MyMetaObjectHandlerTest {

    @Test
    @DisplayName("创建处理器实例")
    void createInstance() {
        MyMetaObjectHandler handler = new MyMetaObjectHandler();
        assertNotNull(handler);
        // MetaObjectHandler的insertFill/updateFill需要真实的MyBatis MetaObject
        // 在单元测试中无法完全模拟，由集成测试覆盖
    }
}
