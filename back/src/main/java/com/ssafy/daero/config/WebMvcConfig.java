package com.ssafy.daero.config;

import com.ssafy.daero.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/admin")
                .excludePathPatterns("/users/signup")
                .excludePathPatterns("/users/email")
                .excludePathPatterns("/users/email/**")
                .excludePathPatterns("/users/**/verified")
                .excludePathPatterns("/users/nickname")
                .excludePathPatterns("/users/login")
                .excludePathPatterns("/users/**/profile")
                .excludePathPatterns("/users/**/badges")
                .excludePathPatterns("/users/reset-password")
                .excludePathPatterns("/users/reset-password/*")
                .excludePathPatterns("/users/**/reset-password")
                .excludePathPatterns("/users/**/duplicate")
                .excludePathPatterns("/users/**/fcm-token");
        // TODO: 회원 탈퇴, 가입 여부 확인 URL 변경 필요.
    }

    @Bean
    protected JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
}