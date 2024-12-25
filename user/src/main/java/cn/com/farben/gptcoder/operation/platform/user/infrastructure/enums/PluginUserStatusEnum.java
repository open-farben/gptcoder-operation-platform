package cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 插件用户状态枚举
 */
@Getter
public enum PluginUserStatusEnum {
    ENABLE("enable", "启用"), DISABLE("disable", "禁用");

    PluginUserStatusEnum(String state, String describe) {
        this.state = state;
        this.describe = describe;
    }

    @EnumValue
    private final String state;
    private final String describe;
}
