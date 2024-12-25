package cn.com.farben.commons.file.infrastructure.enums;

import lombok.Getter;

/**
 * 文件改变类型枚举
 */
@Getter
public enum FileChangeTypeEnum {
    CREATE("创建"), CHANGE("修改"), REMOVE("删除"), FORCE_REMOVE_FOLDER("强制删除目录"),
    FORCE_REMOVE_FOLDER_DESCENDANT("强制删除目录记录的子孙文件");

    FileChangeTypeEnum(String describe) {
        this.describe = describe;
    }

    private final String describe;
}
