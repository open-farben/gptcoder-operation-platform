package cn.com.farben.gptcoder.operation.platform;

import cn.com.farben.gptcoder.operation.platform.listener.AfterConfigListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 运营平台启动类
 *
 */
@EnableScheduling
@EnableAsync
@SpringBootApplication(scanBasePackages = { "cn.com.farben.gptcoder.operation", "cn.com.farben.commons" },
        exclude = { SecurityAutoConfiguration.class })
public class GptcoderOperationPlatformApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(GptcoderOperationPlatformApplication.class);
        springApplication.addListeners(new AfterConfigListener());
        springApplication.run(args);
    }
}
