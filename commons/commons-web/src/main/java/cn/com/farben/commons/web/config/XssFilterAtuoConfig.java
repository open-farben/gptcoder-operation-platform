package cn.com.farben.commons.web.config;

import cn.com.farben.commons.web.filter.SimpleCORSFilter;
import cn.com.farben.commons.web.utils.XssStringJsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class XssFilterAtuoConfig {
    /**
     * 注册自定义过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean xssFiltrRegister() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        //设置系统过滤器 (setFilter就是你所定义的过滤器filter类)
        registration.setFilter(new SimpleCORSFilter());
        //过滤所有路径
        registration.addUrlPatterns("/*");
        //过滤器名称
        registration.setName("XssFilter");
        //优先级
        registration.setOrder(1);
        return registration;
    }

    /**
     * 过滤JSON数据
     * @return
     */
    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        SimpleModule module = new SimpleModule();
        //自定义序列化过滤配置(XssStringJsonDeserializer), 对入参进行转译
        module.addDeserializer(String.class, new XssStringJsonDeserializer());
        // 注册解析器
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json().build();
        objectMapper.registerModule(module);
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }
}
