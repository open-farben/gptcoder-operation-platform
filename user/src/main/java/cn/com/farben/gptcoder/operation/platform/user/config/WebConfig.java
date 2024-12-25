package cn.com.farben.gptcoder.operation.platform.user.config;


import cn.com.farben.commons.web.config.BaseExchangeClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class WebConfig extends BaseExchangeClientConfig {
    @Value("${coder.config.url.apisix}")
    private String apisixurl;
    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    public WebConfig(Environment environment) {
        super(environment);
    }

}