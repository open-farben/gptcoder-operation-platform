package cn.com.farben.gptcoder.operation.platform.listener;

import cn.com.farben.commons.web.utils.RsaUtils;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import cn.hutool.crypto.asymmetric.KeyType;
import org.springframework.lang.NonNull;
import org.apache.poi.ss.SpreadsheetVersion;

import java.lang.reflect.Field;

public class AfterConfigListener implements SmartApplicationListener, Ordered {
    private static final Log logger = LogFactory.get();
    public boolean supportsEventType(@NonNull Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType) || ApplicationReadyEvent.class.isAssignableFrom(eventType);
    }
    public void onApplicationEvent(@NonNull ApplicationEvent event) {

        SpreadsheetVersion excel2007 = SpreadsheetVersion.EXCEL2007;
        if (Integer.MAX_VALUE != excel2007.getMaxTextLength()) {
            Field field;
            try {
                // SpreadsheetVersion.EXCEL2007的_maxTextLength变量
                field = excel2007.getClass().getDeclaredField("_maxTextLength");
                // 关闭反射机制的安全检查，可以提高性能
                field.setAccessible(true);
                // 重新设置这个变量属性值
                field.set(excel2007,Integer.MAX_VALUE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void decryptConfig(ApplicationEnvironmentPreparedEvent environmentPreparedEvent) {
        // 获得配置文件的内容
        String url = environmentPreparedEvent.getEnvironment().getProperty("spring.datasource.url");
        String username = environmentPreparedEvent.getEnvironment().getProperty("spring.datasource.username");
        String password = environmentPreparedEvent.getEnvironment().getProperty("spring.datasource.password");
        String host = environmentPreparedEvent.getEnvironment().getProperty("spring.data.redis.host");
        String redisPassword = environmentPreparedEvent.getEnvironment().getProperty("spring.data.redis.password");
        String pluginUserDefaultPassword = environmentPreparedEvent.getEnvironment().getProperty("coder.user.plugin-user-default-password");

        if (CharSequenceUtil.isNotBlank(url) && url.startsWith(RsaUtils.CONFIG_KEY)) {
            // 解密数据库url
            String decryptedUrl = RsaUtils.decryptStr(url.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey);
            System.setProperty("spring.datasource.url", decryptedUrl);
            logger.info("解密后的数据库URL: " + decryptedUrl);
        }
        if (CharSequenceUtil.isNotBlank(username) && username.startsWith(RsaUtils.CONFIG_KEY)) {
            // 解密数据库用户
            String decryptedUsername = RsaUtils.decryptStr(username.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey);
            System.setProperty("spring.datasource.username", decryptedUsername);
            logger.info("解密后的数据库用户名: " + decryptedUsername);
        }
        if (CharSequenceUtil.isNotBlank(password) && password.startsWith(RsaUtils.CONFIG_KEY)) {
            // 解密密码
            String decryptedPassword = RsaUtils.decryptStr(password.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey);
            System.setProperty("spring.datasource.password", decryptedPassword);
            logger.info("解密后的数据库密码: " + decryptedPassword);
        }
        if (CharSequenceUtil.isNotBlank(host) && host.startsWith(RsaUtils.CONFIG_KEY)) {
            // 解密redis主机
            String decryptedHost = RsaUtils.decryptStr(host.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey);
            System.setProperty("spring.data.redis.host", decryptedHost);
            logger.info("解密后的Redis主机: " + decryptedHost);
        }
        if (CharSequenceUtil.isNotBlank(redisPassword) && redisPassword.startsWith(RsaUtils.CONFIG_KEY)) {
            // 解密redis密码
            String decryptedRedisPassword = RsaUtils.decryptStr(redisPassword.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey);
            System.setProperty("spring.data.redis.password", decryptedRedisPassword);
            logger.info("解密后的Redis密码: " + decryptedRedisPassword);
        }
        if (CharSequenceUtil.isNotBlank(pluginUserDefaultPassword) && pluginUserDefaultPassword.startsWith(RsaUtils.CONFIG_KEY)) {
            // 解密插件用户默认密码
            String decryptedPluginUserDefaultPassword = RsaUtils.decryptStr(pluginUserDefaultPassword.replaceFirst(RsaUtils.CONFIG_KEY, ""), KeyType.PrivateKey);
            System.setProperty("coder.user.plugin-user-default-password", decryptedPluginUserDefaultPassword);
            logger.info("解密后的插件用户默认密码: " + decryptedPluginUserDefaultPassword);
        }
    }

    @Override
    public int getOrder() {
        // 写在加载配置文件之后
        return 1;
    }
}
