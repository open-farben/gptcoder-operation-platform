package cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;

/**
 * 用户使用标记枚举
 * @author Administrator
 * @version 1.0
 * @title DeleteEnum
 * @create 2023/7/21 17:12
 */
public enum UseFlagEnum {
    NORMAL((byte)0, "正常"),
    DISABLE((byte)1, "禁用");

    private final Byte code;

    private final String desc;

    UseFlagEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Byte getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}
