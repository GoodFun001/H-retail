package com.retail.config;

import com.retail.config.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 确保导入 HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * 安全配置类
 *
 * 规则：
 * 1. 只对 POST /api/orders 接口要求用户登录（通过JWT认证）。
 * 2. 所有其他接口（包括登录、注册、查询商品、查询订单列表等）都允许匿名访问。
 * 3. 禁用CSRF，因为我们使用JWT。
 * 4. 不创建Session，使用无状态认证。
 * 5. 配置CORS，允许来自前端和Swagger的跨域请求。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 禁用CSRF
            .csrf().disable()

            // 2. 配置URL访问权限 (核心修改点)
            .authorizeHttpRequests(authorize -> authorize
                // 允许访问静态资源
                .antMatchers("/api/uploads/**").permitAll()
                // =================================================================
                // ==                      核心修改点在这里                        ==
                // =================================================================
                // 规则A: 明确指定只对 "POST /api/orders" 这个请求要求认证
                .antMatchers(HttpMethod.POST, "/api/orders").authenticated()

                // 规则B: 所有其他请求都允许匿名访问
                .anyRequest().permitAll()
            )

            // 3. 配置会话管理为无状态
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            // 4. 添加JWT过滤器
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 配置CORS（跨域资源共享）
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
