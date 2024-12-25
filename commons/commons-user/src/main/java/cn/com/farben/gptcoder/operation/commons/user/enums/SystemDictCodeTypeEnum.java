package cn.com.farben.gptcoder.operation.commons.user.enums;

import java.util.Arrays;

/**
 * 系统字典定义枚举类型
 * @author wuanhui
 */
public enum SystemDictCodeTypeEnum {

    JOB_INFO("JOB_INFO", "职务信息"),
    PROMPT_THOUGHT_CHAIN("prompt_thought_chain", "提示词信息"),
    TOKEN_REFRESH_TIME("token_refresh_time", "token刷新时间"),
    POSITION_INFO("POSITION_INFO", "岗位信息");

    private final String code;

    private final String desc;

    SystemDictCodeTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public static SystemDictCodeTypeEnum exist(String code) {
        return Arrays.stream(SystemDictCodeTypeEnum.values()).filter(e -> e.code.equals(code)).findAny().orElse(null);
    }
}
