package cn.com.farben.gptcoder.operation.platform.group.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 *
 * 工作组变更事件<br>
 *
 */
public class GroupChangeEvent extends ApplicationEvent {
    public GroupChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
