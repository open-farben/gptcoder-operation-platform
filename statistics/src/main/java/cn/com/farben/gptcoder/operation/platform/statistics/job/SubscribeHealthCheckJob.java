package cn.com.farben.gptcoder.operation.platform.statistics.job;

import cn.com.farben.gptcoder.operation.platform.statistics.application.component.UsageStreamSubscribe;
import cn.com.farben.gptcoder.operation.platform.statistics.config.RedisStreamConfiguration;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.RedisConstants;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * 订阅者自我检查任务，每20分钟一次
 */
@Component
public class SubscribeHealthCheckJob extends QuartzJobBean {
    private static final Log logger = LogFactory.get();
    private final StringRedisTemplate redisTemplate;
    private final UsageStreamSubscribe usageStreamSubscribe;
    private final RedisStreamConfiguration redisStreamConfiguration;

    public SubscribeHealthCheckJob(StringRedisTemplate redisTemplate, UsageStreamSubscribe usageStreamSubscribe, RedisStreamConfiguration redisStreamConfiguration) {
        this.redisTemplate = redisTemplate;
        this.usageStreamSubscribe = usageStreamSubscribe;
        this.redisStreamConfiguration = redisStreamConfiguration;
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        logger.info("------订阅者自我健康检查任务启动------");
        StreamOperations<String, String, String> streamOperations = redisTemplate.opsForStream();
        String consumerName = usageStreamSubscribe.getConsumerName();
        StreamInfo.XInfoConsumers consumers = streamOperations.consumers(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY);
        boolean aliveFlg = false;
        for (StreamInfo.XInfoConsumer xInfoConsumer : consumers) {
            if (consumerName.equals(xInfoConsumer.consumerName())) {
                aliveFlg = true;
                break;
            }
        }
        if (!aliveFlg) {
            // 重新订阅
            redisStreamConfiguration.joinGroup();
        }
    }
}
