package cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure;

/**
 * 插件常量类
 */
public class PluginConstants {
    /** 临时存储的前缀 */
    public static final String TMP_STORE_PRIFIX = "plugin_";

    /** 插件文件的前缀 */
    public static final String PLUGIN_FILE_PRIFIX = "GPTCoder";

    /** 插件状态字段名 */
    public static final String PLUGIN_STATUS_COLUMN_NAME = "status";

    /** 插件创建时间字段名 */
    public static final String CREATE_TIME_COLUMN_NAME = "create_time";

    /** 插件版本号缓存信息 */
    public static final String PLUGIN_VERSION_CACHE_KEY = "plugin_version_template_key";

    /** 版本数限制 */
    public static final int PLUGIN_VERSION_LIMIT = 500;

    private PluginConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
