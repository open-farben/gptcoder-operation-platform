package cn.com.farben.gptcoder.operation.platform.feedback.domain;

import cn.com.farben.commons.ddd.repository.entity.DataChangeRecord;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.entity.FeedbackEntity;
import cn.com.farben.gptcoder.operation.platform.feedback.domain.event.FeedbackChangeEvent;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.enums.FeedbackEnum;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.facade.FeedbackRepository;
import cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.po.FeedbackPO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.mybatisflex.core.query.QueryColumn;
import lombok.Getter;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static cn.com.farben.gptcoder.operation.platform.feedback.infrastructure.repository.po.table.FeedbackTableDef.FEEDBACK;

/**
 * 用户反馈聚合根
 */
@Getter
public class FeedbackAggregateRoot {
    private static final Log logger = LogFactory.get();

    private final FeedbackRepository feedbackRepository;

    /** 事件发布 */
    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 效果反馈
     * @param feedbackEntity 用户反馈实体
     */
    public void feedbackEffect(FeedbackEntity feedbackEntity) {
        FeedbackEntity feedbackDBEntity = feedbackRepository.getFeedbackById(feedbackEntity.getId());
        boolean existsFlg = Objects.nonNull(feedbackDBEntity) && CharSequenceUtil.isNotBlank(feedbackDBEntity.getId());
        feedbackEntity.setFeedbackTime(LocalDateTime.now());
        String id = feedbackEntity.getId();

        if (FeedbackEnum.CANCEL == feedbackEntity.getFeedbackType()) {
            // 取消反馈
            feedbackRepository.removeFeedback(id);
            return;
        }
        if (existsFlg) {
            if (feedbackDBEntity.getFeedbackType() == feedbackEntity.getFeedbackType()) {
                // 效果没有改变
                return;
            }
            Map<QueryColumn, Object> valueMap = new HashMap<>();
            Map<String, Object> whereConditions = Map.of(FEEDBACK.ID.getName(), id);
            valueMap.put(FEEDBACK.FEEDBACK_TYPE, feedbackEntity.getFeedbackType());
            valueMap.put(FEEDBACK.FEEDBACK_TIME, LocalDateTime.now());

            updateFeedback(valueMap, whereConditions);
            return;
        }
        // 插入数据
        feedbackRepository.saveFeedback(feedbackEntity);
    }

    /**
     * 建议反馈
     * @param feedbackEntity 用户反馈实体
     */
    public void feedbackSuggest(FeedbackEntity feedbackEntity) {
        FeedbackEntity feedbackDBEntity = feedbackRepository.getFeedbackById(feedbackEntity.getId());
        boolean existsFlg = Objects.nonNull(feedbackDBEntity) && CharSequenceUtil.isNotBlank(feedbackDBEntity.getId());
        feedbackEntity.setFeedbackTime(LocalDateTime.now());
        if (existsFlg) {
            // 更新数据
            if (CharSequenceUtil.equals(feedbackDBEntity.getSuggest(), feedbackEntity.getSuggest())) {
                // 效果没有改变
                return;
            }
            Map<QueryColumn, Object> valueMap = new HashMap<>();
            Map<String, Object> whereConditions = Map.of(FEEDBACK.ID.getName(), feedbackEntity.getId());
            valueMap.put(FEEDBACK.SUGGEST, feedbackEntity.getSuggest());
            valueMap.put(FEEDBACK.FEEDBACK_TIME, LocalDateTime.now());

            updateFeedback(valueMap, whereConditions);
        } else {
            // 插入数据
            feedbackRepository.saveFeedback(feedbackEntity);
        }
    }

    public static class Builder {
        /** 用户反馈仓储接口 */
        private final FeedbackRepository feedbackRepository;

        /** 事件发布 */
        private final ApplicationEventPublisher applicationEventPublisher;

        public Builder(FeedbackRepository feedbackRepository, ApplicationEventPublisher applicationEventPublisher) {
            this.feedbackRepository = feedbackRepository;
            this.applicationEventPublisher = applicationEventPublisher;
        }

        public FeedbackAggregateRoot build() {
            return new FeedbackAggregateRoot(this);
        }
    }

    private void updateFeedback(Map<QueryColumn, Object> valueMap, Map<String, Object> whereConditions) {
        applicationEventPublisher.publishEvent(new FeedbackChangeEvent(new DataChangeRecord<>(FeedbackPO.class, valueMap,
                null, whereConditions, null, null)));
    }

    private FeedbackAggregateRoot(Builder builder) {
        this.feedbackRepository = builder.feedbackRepository;
        this.applicationEventPublisher = builder.applicationEventPublisher;
    }
}
