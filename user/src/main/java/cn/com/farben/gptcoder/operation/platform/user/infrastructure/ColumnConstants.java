package cn.com.farben.gptcoder.operation.platform.user.infrastructure;

/**
 * 常用字段常量类
 */
public class ColumnConstants {
    /** 用户账号字段 */
    public static final String USER_ACCOUNT_COLUMN = "account";

    /** 更新人字段 */
    public static final String UPDATE_USER_COLUMN = "update_user";

    private ColumnConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
