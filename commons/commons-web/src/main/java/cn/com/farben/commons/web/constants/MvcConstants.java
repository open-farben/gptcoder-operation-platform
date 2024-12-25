package cn.com.farben.commons.web.constants;

/**
 * mvc相关常量类
 */
public class MvcConstants {
    /** 每页最大数据量 */
    public static final int MAX_PAGE_SIZE = 100;

    /** 默认用户 */
    public static final String DEFAULT_USER = "system";

    /** 创建者列名 */
    public static final String CREATE_USER_COLUMN_NAME = "create_user";

    /** 创建时间列名 */
    public static final String CREATE_TIME_COLUMN_NAME = "create_time";

    /** 更新者列名 */
    public static final String UPDATE_USER_COLUMN_NAME = "update_user";

    /** 更新时间列名 */
    public static final String UPDATE_TIME_COLUMN_NAME = "update_time";

    private MvcConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
