package cn.com.farben.gptcoder.operation.platform.plugin.version.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.mapper"})
public class PluginVersionMybatisPlusConfig {
}
