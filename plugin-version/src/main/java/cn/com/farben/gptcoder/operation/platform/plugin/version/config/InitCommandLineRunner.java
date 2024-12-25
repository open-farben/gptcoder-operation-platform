package cn.com.farben.gptcoder.operation.platform.plugin.version.config;

import cn.com.farben.gptcoder.operation.platform.plugin.version.application.service.PluginCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 系统初始化加载
 * @author Administrator
 * @version 1.0
 * @title InitCommandLineRunner
 * @create 2023/8/7 15:32
 */
@Slf4j
@Component
public class InitCommandLineRunner implements CommandLineRunner {

    private final PluginCacheService pluginCacheService;

    public InitCommandLineRunner(PluginCacheService pluginCacheService) {
        this.pluginCacheService = pluginCacheService;
    }

    @Override
    public void run(String... args) {
        log.info(" ------------- 初始化加载版本缓存：----------------");
        pluginCacheService.initPluginCache();
    }
}
