package com.retail.config;

import com.retail.util.JwtUtil;
import com.retail.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
        
        String token = getTokenFromRequest(request);
        log.info("请求URL: {}", request.getRequestURI());
        
        if (token != null && !jwtUtil.isTokenExpired(token)) {
            // =================================================================
            // ==                      核心修改点在这里                        ==
            // =================================================================
            // 将 Redis Key 的前缀设置为 "user:session:"
            // 以匹配 UserServiceImpl 中存入的键
            String userKey = "user:session:" + token;
            Object userInfo = redisUtil.get(userKey);
            
            if (userInfo != null) {
                log.info("从Redis获取的用户信息: {}", userInfo);

                // 为用户创建一个默认的权限列表
                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
                
                // 创建认证对象，并传入权限列表
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userInfo, null, authorities);
                
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置到Security上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("用户认证成功，并授予 ROLE_USER 权限，设置了SecurityContext");
            } else {
                log.warn("Redis中未找到用户会话，Token可能已失效或被手动登出。Key: {}", userKey);
            }
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}