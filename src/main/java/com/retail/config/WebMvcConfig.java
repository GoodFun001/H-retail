package com.retail.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置静态资源访问路径
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置uploads目录为静态资源路径（使用绝对路径确保Windows系统正常工作）
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        // 注意：context-path已设置为/api，所以资源处理器路径只需要/uploads/**
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(31536000); // 缓存一年
    }
}
