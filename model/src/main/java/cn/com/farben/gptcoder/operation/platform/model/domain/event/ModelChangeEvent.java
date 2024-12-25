package cn.com.farben.gptcoder.operation.platform.model.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 *
 * 模型变更事件<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
public class ModelChangeEvent extends ApplicationEvent {
    public ModelChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
