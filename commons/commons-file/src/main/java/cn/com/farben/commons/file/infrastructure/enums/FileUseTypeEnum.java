package cn.com.farben.commons.file.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 文件使用类型枚举
 */
@Getter
public enum FileUseTypeEnum {
    PLUGIN("plugin", "插件文件", "plugin"), KNOWLEDGE("knowledge", "知识库", "knowledge"),
    GIT("git", "git仓库", "git");

    FileUseTypeEnum(String type, String describe, String storePath) {
        this.type = type;
        this.describe = describe;
        this.storePath = storePath;
    }

    /** 使用类型 */
    @EnumValue
    private final String type;

    /** 描述 */
    private final String describe;

    /** 文件存储路径 */
    private final String storePath;
}
