package cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;

/**
 * 系统角色配置枚举
 * @author wuanhui
 */
public enum SystemRoleEnum {
    ADMINISTRATOR("administrator", "超级管理员"),
    NORMAL("normal", "普通用户");

    private final String code;

    private final String desc;

    SystemRoleEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
