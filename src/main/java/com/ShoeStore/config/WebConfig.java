package com.ShoeStore.config;

import com.ShoeStore.interceptor.AdminAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Áp dụng Interceptor cho tất cả các đường dẫn bắt đầu bằng /admin/
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**");
        // Thêm .excludePathPatterns("/admin/login") nếu trang login admin nằm chung
        // path,
        // nhưng hiện tại AuthController login path là /login.
    }
}
