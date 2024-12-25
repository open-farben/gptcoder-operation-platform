package cn.com.farben.commons.web.config;

import cn.com.farben.commons.web.interceptor.LogInterceptor;
import cn.com.farben.commons.web.interceptor.UserInfoInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 */
@Configuration
public class InterceptorConfigurer implements WebMvcConfigurer {
    private final LogInterceptor logInterceptor;
    private final UserInfoInterceptor userInfoInterceptor;

    @Value("${config.interceptor.enableUserInterceptor:true}")
    private boolean enableUserInterceptor;

    public InterceptorConfigurer(LogInterceptor logInterceptor, UserInfoInterceptor userInfoInterceptor) {
        this.logInterceptor = logInterceptor;
        this.userInfoInterceptor = userInfoInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 日志拦截器
        registry.addInterceptor(logInterceptor).addPathPatterns("/**").excludePathPatterns("/error/**").order(0);
        if (enableUserInterceptor) {
            // 用户信息拦截器
            registry.addInterceptor(userInfoInterceptor).addPathPatterns("/**").excludePathPatterns("/error/**").order(1);
        }
    }
}
