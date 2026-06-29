package com.emras.shared.config.web;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
/**
 * Registers the RequestLoggingInterceptor with Spring MVC.
 * Excludes actuator and static resource paths from logging
 * to keep logs clean.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/actuator/**",
                        "/swagger-ui/**",
                        "/api-docs/**"
                );
    }
}