package cn.com.farben.gptcoder.operation.platform.feedback.domain.event;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import org.springframework.context.ApplicationEvent;

/**
 * 用户反馈变更事件
 */
public class FeedbackChangeEvent extends ApplicationEvent {
    public FeedbackChangeEvent(DataChangeRecord source) {
        super(source);
    }
}
