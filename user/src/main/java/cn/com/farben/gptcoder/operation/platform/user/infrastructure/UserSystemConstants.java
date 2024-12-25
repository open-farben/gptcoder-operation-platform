package cn.com.farben.gptcoder.operation.platform.user.infrastructure;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 常用字段常量类
 */
public class UserSystemConstants {

    /** 插件用户excel模板地址 */
    public static final String USER_EXCEL_IMPORT_ADDRESS = "templates/importTemplate.xlsx";

    /** 插件用户excel模板地址 */
    public static final String USER_EXCEL_ERROR_ADDRESS = "templates/errorTemplate.xlsx";

    /** 上传有问题的数据缓存key */
    public static final String USER_EXCEL_IMPORT_CACHE_KEY = "errorDataKey_";

    public static final String USER_EXCEL_IMPORT_FIELD_STATUS = "启用";

    public static final String USER_EXCEL_IMPORT_FIELD_DISABLE = "禁用";

    public static final List<String> IMPORT_FIELD_DISABLE_LIST = Lists.newArrayList(USER_EXCEL_IMPORT_FIELD_STATUS, USER_EXCEL_IMPORT_FIELD_DISABLE);

    public static final String USER_EXCEL_IMPORT_XLSX = ".xlsx";

    public static final String USER_EXCEL_IMPORT_XLS = ".xls";

    public static final String EXIST_ROLE_AUTH_STRING = "exist";

    public static final String NO_DATA_PERMISSION_MESSAGE = "您没有对应机构的数据权限";

    private UserSystemConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
