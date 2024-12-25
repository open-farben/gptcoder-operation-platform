package cn.com.farben.commons.web.config;

import cn.com.farben.commons.web.filter.TrimFilter;
import jakarta.servlet.DispatcherType;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

//@Configuration
//public class TrimFilterConfig {
//    /**
//     * 注册去除参数头尾空格过滤器
//     */
//    @Bean
//    public FilterRegistrationBean trimFilterRegistration() {
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setDispatcherTypes(DispatcherType.REQUEST);
//        //注册自定义过滤器
//        registration.setFilter(new TrimFilter());
//        //过滤所有路径
//        registration.addUrlPatterns("/*");
//        //过滤器名称
//        registration.setName("trimFilter");
//        //优先级越低越优先，这里说明最低优先级
//        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
//        return registration;
//    }
//}
