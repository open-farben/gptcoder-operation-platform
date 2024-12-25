package cn.com.farben.gptcoder.operation.commons.auth.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.mapper"})
public class LicenseMybatisPlusConfig {
}
