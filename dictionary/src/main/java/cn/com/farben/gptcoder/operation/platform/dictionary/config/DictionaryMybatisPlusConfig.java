package cn.com.farben.gptcoder.operation.platform.dictionary.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = {"cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.mapper"})
public class DictionaryMybatisPlusConfig {
}
