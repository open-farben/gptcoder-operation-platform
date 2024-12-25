package cn.com.farben.commons.file.infrastructure;

import cn.com.farben.commons.file.infrastructure.enums.FileSyncTypeEnum;

import java.time.LocalDateTime;

/**
 * 文件同步信息类
 */
public record FileSyncInfo(FileSyncTypeEnum syncTypeEnum, LocalDateTime eventTime, byte folder, String path) {
}
