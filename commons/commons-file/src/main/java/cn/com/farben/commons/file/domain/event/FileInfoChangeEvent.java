package cn.com.farben.commons.file.domain.event;

import cn.com.farben.commons.ddd.domain.event.DataChangeEvent;
import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;

/**
 * 文件信息变更事件<br>
 * 数据库信息变更，内部使用
 */
public class FileInfoChangeEvent extends DataChangeEvent {
    public FileInfoChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
