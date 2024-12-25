package cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 模型状态枚举
 */
@Getter
public enum ModelStatusEnum {
    ENABLE("enable", "启用"), DISABLE("disable", "禁用");

    ModelStatusEnum(String state, String describe) {
        this.state = state;
        this.describe = describe;
    }

    @EnumValue
    private final String state;
    private final String describe;
}
