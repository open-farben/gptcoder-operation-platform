package cn.com.farben.gptcoder.operation.platform.group.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.mapper"})
public class GroupMybatisPlusConfig {
}
