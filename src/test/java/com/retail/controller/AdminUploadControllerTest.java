package com.retail.controller;

import com.retail.common.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminUploadController 测试")
class AdminUploadControllerTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private AdminUploadController controller;

    @BeforeEach
    void setUp() {
        // 使用系统临时目录，确保transferTo能成功
        ReflectionTestUtils.setField(controller, "uploadPath", System.getProperty("java.io.tmpdir") + "/test-uploads");
        ReflectionTestUtils.setField(controller, "baseUrl", "http://localhost:8080");
    }

    @Test
    @DisplayName("uploadFile - 空文件")
    void uploadFile_EmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", "image/jpeg", new byte[0]);
        Result<String> result = controller.uploadFile(file);
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertTrue(result.getMsg().contains("请选择要上传的文件"));
    }

    @Test
    @DisplayName("uploadFile - 非图片文件")
    void uploadFile_NonImage() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "hello".getBytes());
        Result<String> result = controller.uploadFile(file);
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertTrue(result.getMsg().contains("只支持图片文件上传"));
    }

    @Test
    @DisplayName("uploadFile - contentType为null")
    void uploadFile_NullContentType() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.jpg", null, "content".getBytes());
        Result<String> result = controller.uploadFile(file);
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertTrue(result.getMsg().contains("只支持图片文件上传"));
    }

    @Test
    @DisplayName("uploadFile - 文件超过5MB")
    void uploadFile_TooLarge() {
        byte[] largeContent = new byte[6 * 1024 * 1024];
        MockMultipartFile file = new MockMultipartFile(
                "file", "large.jpg", "image/jpeg", largeContent);
        Result<String> result = controller.uploadFile(file);
        assertNotNull(result);
        assertEquals(400, result.getCode());
        assertTrue(result.getMsg().contains("文件大小不能超过5MB"));
    }

    @Test
    @DisplayName("uploadFile - 成功上传图片")
    void uploadFile_Success() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "photo.jpg", "image/jpeg", "test image content".getBytes());
        Result<String> result = controller.uploadFile(file);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }
}
