package cn.com.farben.gptcoder.operation.platform.statistics.job;

import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.RedisConstants;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * 消费者健康检查任务，每小时一次，不允许并行
 */
@Component
@DisallowConcurrentExecution
public class ConsumerHealthCheckJob extends QuartzJobBean {
    private static final Log logger = LogFactory.get();
    private final StringRedisTemplate redisTemplate;

    public ConsumerHealthCheckJob(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        logger.info("------消费者健康检查任务启动------");
        handlePendingConsumer();
        handleInertiaConsumer();
    }

    /**
     * 处理有Pending消息的消费者
     */
    private void handlePendingConsumer() {
        StreamOperations<String, String, String> streamOperations = redisTemplate.opsForStream();

        PendingMessagesSummary summary = streamOperations.pending(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY);
        if (Objects.isNull(summary)) {
            // 没有任何Pending消息
            return;
        }
        long totalPendingMessages = summary.getTotalPendingMessages();
        if (totalPendingMessages == 0) {
            // 没有任何Pending消息
            return;
        }

        Map<String, Long> pendingMessagesPerConsumer = summary.getPendingMessagesPerConsumer();
        for (Map.Entry<String, Long> entry : pendingMessagesPerConsumer.entrySet()) {
            String consumerName = entry.getKey();
            Long value = entry.getValue();
            logger.info("消费者[{}]，一共有[{}]条pending消息，将被移除", consumerName, value);
            streamOperations.deleteConsumer(RedisConstants.GPTCODER_USAGE_STREAM_KEY,
                    Consumer.from(RedisConstants.STREAM_GROUP_KEY, consumerName));
        }
    }

    /**
     * 处理不活跃的消费者
     */
    private void handleInertiaConsumer() {
        StreamOperations<String, String, String> streamOperations = redisTemplate.opsForStream();

        StreamInfo.XInfoConsumers consumers = streamOperations.consumers(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY);
        for (StreamInfo.XInfoConsumer xInfoConsumer : consumers) {
            long idleTimeMs = xInfoConsumer.idleTimeMs();
            if (idleTimeMs >= RedisConstants.CONSUMER_MAX_IDLE_TIME) {
                // idle时间大于两小时，可认为消费者已不再活跃
                streamOperations.deleteConsumer(RedisConstants.GPTCODER_USAGE_STREAM_KEY, Consumer.from(xInfoConsumer.groupName(), xInfoConsumer.consumerName()));
            }
        }
    }
}
