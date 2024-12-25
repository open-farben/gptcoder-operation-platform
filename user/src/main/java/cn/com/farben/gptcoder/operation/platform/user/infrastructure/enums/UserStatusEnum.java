package cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
public enum UserStatusEnum {
    NORMAL("normal", "正常"), DISABLE("disable", "禁用")
    , EXPIRED("expired", "过期"), LOCK("lock", "锁定");

    UserStatusEnum(String state, String describe) {
        this.state = state;
        this.describe = describe;
    }

    @EnumValue
    private final String state;
    private final String describe;
}
