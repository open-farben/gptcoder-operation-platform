package cn.com.farben.commons.web.listener;

import cn.com.farben.commons.web.utils.RsaUtils;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ApplicationEnvironmentPreparedListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    private static final Log logger = LogFactory.get();

    @Override
    public void onApplicationEvent(@NonNull ApplicationEnvironmentPreparedEvent event) {
        decryptConfig(event);
    }

    private void decryptConfig(ApplicationEnvironmentPreparedEvent environmentPreparedEvent) {
        // 获得配置文件的内容
        ConfigurableEnvironment environment = environmentPreparedEvent.getEnvironment();

        MutablePropertySources propertySources = environment.getPropertySources();
        // 获取所有配置
        Map<String, String> props = StreamSupport.stream(propertySources.spliterator(), false)
                .filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .filter(s -> {
                    String propValue = environment.getProperty(s);
                    return CharSequenceUtil.isNotBlank(propValue) && propValue.startsWith(RsaUtils.CONFIG_KEY);
                })
                .collect(Collectors.toMap(Function.identity(), environment::getProperty));

        props.keySet().forEach(k -> {
            String propValue = props.get(k);
            logger.info("解密配置项[{}]", k);
            // 解密配置项
            System.setProperty(k, RsaUtils.decryptStr(propValue.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey));
        });
    }
}
