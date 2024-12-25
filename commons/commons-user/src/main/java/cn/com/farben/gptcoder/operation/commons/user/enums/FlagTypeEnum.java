package cn.com.farben.gptcoder.operation.commons.user.enums;

import java.util.Arrays;

/**
 * 数据库标记枚举值
 * @author wuanhui
 */
public enum FlagTypeEnum {

    /**
     * 0 表示启用（disable）、正常（delete）、常量字典（dict_type）等
     * 1 表示禁用（disable）、删除（delete）、级联字典（dict_type）等
     */
    ZERO((byte)0, "0"),
    ONE((byte)1, "1");

    private final Byte code;

    private final String desc;

    FlagTypeEnum(Byte code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Byte getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static FlagTypeEnum exist(Byte code) {
        return Arrays.stream(FlagTypeEnum.values()).filter(e -> e.code.equals(code)).findAny().orElse(null);
    }
}
