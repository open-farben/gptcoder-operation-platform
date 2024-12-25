package cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 插件状态枚举
 */
@Getter
public enum PluginStatusEnum {
    UNPUBLISHED("unpublished", "未发布"), RELEASED("released", "已发布"), REPEAL("repeal", "撤销发布");

    PluginStatusEnum(String state, String describe) {
        this.state = state;
        this.describe = describe;
    }

    @EnumValue
    private final String state;
    private final String describe;
}
