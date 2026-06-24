package com.retail.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Config 配置类测试")
class ConfigTest {

    @Test
    @DisplayName("OpenApiConfig - 创建OpenAPI")
    void openApiConfig() {
        OpenApiConfig config = new OpenApiConfig();
        OpenAPI openAPI = config.customOpenAPI();
        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("在线零售平台 API", openAPI.getInfo().getTitle());
        assertEquals("1.0.0", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("开发团队", openAPI.getInfo().getContact().getName());
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
        assertNotNull(openAPI.getExternalDocs());
        assertEquals("项目文档", openAPI.getExternalDocs().getDescription());
        assertNotNull(openAPI.getSecurity());
        assertEquals(1, openAPI.getSecurity().size());
    }
}
