package cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 用户类型枚举
 */
@Getter
public enum UserTypeEnum {
    PLUG_IN("plug-in", "插件用户"), PLATFORM("platform", "平台用户");

    UserTypeEnum(String userType, String describe) {
        this.userType = userType;
        this.describe = describe;
    }

    @EnumValue
    private final String userType;
    private final String describe;
}
