package cn.com.farben.gptcoder.operation.platform.model.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.mapper"})
public class ModelMybatisPlusConfig {
}
