package cn.com.farben.gptcoder.operation.platform.user.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 *
 * 插件用户变更事件<br>
 * 创建时间：2023/8/24<br>
 * @author ltg
 *
 */
public class PluginUserChangeEvent extends ApplicationEvent {
    public PluginUserChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
