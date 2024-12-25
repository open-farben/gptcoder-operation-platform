package cn.com.farben.commons.file.infrastructure.enums;

import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

/**
 * 文件同步类型枚举
 */
@Getter
public enum FileSyncTypeEnum {
    REMOVE("remove", "需要同步删除");

    FileSyncTypeEnum(String type, String describe) {
        this.type = type;
        this.describe = describe;
    }

    /** 同步类型 */
    @EnumValue
    private final String type;

    /** 描述 */
    private final String describe;
}
