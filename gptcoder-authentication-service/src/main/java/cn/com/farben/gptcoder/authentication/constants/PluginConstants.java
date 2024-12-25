package cn.com.farben.gptcoder.authentication.constants;

public class PluginConstants {
    /** 用户标识头名称 */
    public static final String USER_IDENTIFICATION_HEADER = "Uuid";

    /** 插件版本头名称 */
    public static final String PLUGIN_VERSION_HEADER = "Version";

    /** 插件用户信息redis缓存的前缀 */
    public static final String REDIS_PLUGIN_USER_PREFIX = "plugin_users:";

    /** 版本信息存放的redis的key */
    public static final String VERSION_REDIS_KEY = "plugin_version_template_key";

    private PluginConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
