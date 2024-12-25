package cn.com.farben.gptcoder.operation.platform.model.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 *
 * 模型参数变更事件<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
public class ModelParameterChangeEvent extends ApplicationEvent {
    public ModelParameterChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
