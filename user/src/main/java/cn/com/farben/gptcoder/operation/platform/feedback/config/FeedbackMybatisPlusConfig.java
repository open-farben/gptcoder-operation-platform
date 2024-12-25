package cn.com.farben.gptcoder.operation.platform.feedback.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.mapper"})
public class FeedbackMybatisPlusConfig {
}
