package com.retail.controller;

import com.retail.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 管理员文件上传控制器
 */
@RestController
@RequestMapping("/admin/upload")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "管理员文件上传", description = "管理员上传文件的相关接口")
public class AdminUploadController {

    private final ResourceLoader resourceLoader;

    // 上传文件存储路径
    @Value("${spring.servlet.multipart.location:./uploads}")
    private String uploadPath;

    // 服务器基础URL
    @Value("${server.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * 上传图片文件
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传图片文件", description = "管理员上传商品图片等文件")
    public Result<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.paramError("请选择要上传的文件");
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.paramError("只支持图片文件上传");
            }

            // 检查文件大小（限制5MB）
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize) {
                return Result.paramError("文件大小不能超过5MB");
            }

            // 创建上传目录（按日期）
            String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path uploadDir = Paths.get(uploadPath, dateDir);
            Files.createDirectories(uploadDir);

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String fileExt = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf('.')) : ".jpg";
            String fileName = UUID.randomUUID().toString() + fileExt;

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            file.transferTo(filePath);

            // 构建可访问的URL (使用相对路径，前端会自动根据当前访问地址补全)
            String fileUrl = String.format("/api/uploads/%s/%s", dateDir, fileName);

            log.info("文件上传成功: {} -> {}", originalFilename, fileUrl);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            return Result.error("文件上传失败");
        } catch (Exception e) {
            log.error("文件上传异常: {}", e.getMessage(), e);
            return Result.error("文件上传异常");
        }
    }
}
