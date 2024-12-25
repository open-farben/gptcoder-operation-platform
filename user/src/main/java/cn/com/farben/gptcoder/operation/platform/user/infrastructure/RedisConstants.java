package cn.com.farben.gptcoder.operation.platform.user.infrastructure;

/**
 * redis相关常量类
 */
public class RedisConstants {
    /** 插件用户信息redis缓存的前缀 */
    public static final String REDIS_PLUGIN_USER_PREFIX = "plugin_users:";

    /** 用户登陆信息的key */
    public static final String LOGIN_INFO_KEY = "login_info";

    private RedisConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
