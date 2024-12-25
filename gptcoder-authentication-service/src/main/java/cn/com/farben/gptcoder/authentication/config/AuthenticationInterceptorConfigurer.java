package cn.com.farben.gptcoder.authentication.config;

import cn.com.farben.gptcoder.authentication.interceptor.AddressInterceptor;
import cn.hutool.core.text.StrPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器配置
 */
@Configuration
public class AuthenticationInterceptorConfigurer implements WebMvcConfigurer {
    @Value("${authentication.config.token-urls}")
    private String tokenUrls;

    private final AddressInterceptor addressInterceptor;

    public AuthenticationInterceptorConfigurer(AddressInterceptor addressInterceptor) {
        this.addressInterceptor = addressInterceptor;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        String[] urls = tokenUrls.split(StrPool.COMMA);
        List<String> urlList = new ArrayList<>(urls.length);
        for (String url : urls) {
            urlList.add(url.trim());
        }
        // 令牌相关接口拦截器
        registry.addInterceptor(addressInterceptor).addPathPatterns(urlList).order(0);
    }

}
