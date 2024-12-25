package cn.com.farben.gptcoder.operation.platform.user.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 *
 * 用户账号变更事件<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
public class UserAccountChangeEvent extends ApplicationEvent {
    public UserAccountChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
