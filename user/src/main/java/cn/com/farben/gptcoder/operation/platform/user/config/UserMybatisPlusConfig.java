package cn.com.farben.gptcoder.operation.platform.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper"})
public class UserMybatisPlusConfig {
}
