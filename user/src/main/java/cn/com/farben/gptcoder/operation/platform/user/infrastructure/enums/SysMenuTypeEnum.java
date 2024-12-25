package cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;

import java.util.Arrays;

/**
 * 系统目录类型配置枚举
 * @author wuanhui
 */
public enum SysMenuTypeEnum {
    MENU("C", "菜单"),
    DIRECTORY("M", "目录");

    private final String code;

    private final String desc;

    SysMenuTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static SysMenuTypeEnum exist(String code) {
        return Arrays.stream(SysMenuTypeEnum.values()).filter(e -> e.code.equals(code)).findAny().orElse(null);
    }
}
