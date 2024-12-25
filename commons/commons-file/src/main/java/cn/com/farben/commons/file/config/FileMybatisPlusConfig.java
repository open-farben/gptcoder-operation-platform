package cn.com.farben.commons.file.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * mybatis配置<br>
 *
 */
@Configuration
@MapperScan(basePackages = {"cn.com.farben.commons.file.infrastructure.repository.mapper"})
public class FileMybatisPlusConfig {
}
