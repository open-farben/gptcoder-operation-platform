package cn.com.farben.commons.ddd.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 * 数据变更事件
 */
public class DataChangeEvent extends ApplicationEvent {
    public DataChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
