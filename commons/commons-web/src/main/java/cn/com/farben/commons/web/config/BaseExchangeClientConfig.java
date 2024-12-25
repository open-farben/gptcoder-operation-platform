package cn.com.farben.commons.web.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

@Component
@RequiredArgsConstructor
public class BaseExchangeClientConfig {
    private final Environment environment;

    @Value("${exchange.config.connect-timeout-millis: 30000}")
    private int connectTimeoutMillis;

    @Value("${exchange.config.read-timeout: 10}")
    private int readTimeout;

    @Value("${exchange.config.write-timeout: 10}")
    private int writeTimeout;

    public HttpServiceProxyFactory getFactory() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMillis)  //连接超时
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(readTimeout)); //读超时
                    conn.addHandlerLast(new WriteTimeoutHandler(writeTimeout)); //写超时
                });

        WebClient client = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        return HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client))
                .embeddedValueResolver(environment::resolvePlaceholders) // 使用 environment 来解析 url 配置
                .build();
    }
}
