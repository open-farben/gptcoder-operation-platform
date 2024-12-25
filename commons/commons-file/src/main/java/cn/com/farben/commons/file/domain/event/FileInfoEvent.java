package cn.com.farben.commons.file.domain.event;

import cn.com.farben.commons.file.domain.entity.FileInfoEntity;
import cn.com.farben.commons.file.infrastructure.enums.FileChangeTypeEnum;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 文件信息事件<br>
 * 创建、变更、删除等，对外
 */
@Getter
public class FileInfoEvent extends ApplicationEvent {
    private FileChangeTypeEnum changeType;

    public FileInfoEvent(FileInfoEntity source, FileChangeTypeEnum changeType) {
        super(source);
        this.changeType = changeType;
    }
}
