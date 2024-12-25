package cn.com.farben.gptcoder.operation.platform.user.config;

import cn.com.farben.commons.web.config.BaseExchangeClientConfig;
import cn.com.farben.gptcoder.operation.platform.user.exchange.AuthenticationClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AuthenticationClientConfig extends BaseExchangeClientConfig {
    public AuthenticationClientConfig(Environment environment) {
        super(environment);
    }

    @Bean
    public AuthenticationClient authenticationClient() {
        HttpServiceProxyFactory factory = getFactory();
        return factory.createClient(AuthenticationClient.class);
    }
}
