package cn.com.farben.gptcoder.operation.platform.plugin.version.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 *
 * 插件版本变更事件<br>
 * 创建时间：2023/9/27<br>
 * @author ltg
 *
 */
public class PluginVersionChangeEvent extends ApplicationEvent {
    public PluginVersionChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
