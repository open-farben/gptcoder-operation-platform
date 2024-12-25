package cn.com.farben.gptcoder.operation.platform.statistics.config;

import cn.com.farben.gptcoder.operation.platform.statistics.application.component.UsageStreamSubscribe;
import cn.com.farben.gptcoder.operation.platform.statistics.handler.StreamErrorHandler;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.RedisConstants;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;
import java.util.concurrent.Executors;

@Configuration
public class RedisStreamConfiguration {
    private final UsageStreamSubscribe usageStreamSubscribe;

    private final StringRedisTemplate redisTemplate;

    private static final Log logger = LogFactory.get();

    private final StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer;

    /**
     * 可以同时支持 独立消费 和 消费者组 消费
     * <p>
     * 可以支持动态的 增加和删除 消费者
     * <p>
     * 消费组需要预先创建出来
     *
     * @return StreamMessageListenerContainer
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public StreamMessageListenerContainer<String, ObjectRecord<String, String>> streamMessageListenerContainer() {
        String groupNotExistsMessage = "no such key";

        try {
            // 1.8版本后新stream
            StreamInfo.XInfoGroups groups = redisTemplate.opsForStream().groups(RedisConstants.GPTCODER_USAGE_STREAM_KEY);
            if (groups.isEmpty()) {
                redisTemplate.opsForStream().createGroup(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY);
            }
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains(groupNotExistsMessage) ||
                    e.getCause().getMessage().toLowerCase().contains(groupNotExistsMessage)) {
                redisTemplate.opsForStream().createGroup(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY);
            } else {
                logger.error("创建消费者组失败", e);
                throw e;
            }
        }

        joinGroup();

        return streamMessageListenerContainer;
    }

    public void joinGroup() {
        String consumer = RandomUtil.randomNumbers(5);

        usageStreamSubscribe.setConsumerName(consumer);
        streamMessageListenerContainer.receive(Consumer.from(RedisConstants.STREAM_GROUP_KEY, consumer),
                StreamOffset.create(RedisConstants.GPTCODER_USAGE_STREAM_KEY, ReadOffset.lastConsumed()),
                usageStreamSubscribe);
        logger.info("消费者[{}]加入消费组[{}]，开始消费stream[{}]中的插件使用信息", consumer, RedisConstants.STREAM_GROUP_KEY,
                RedisConstants.GPTCODER_USAGE_STREAM_KEY);
    }

    public RedisStreamConfiguration(RedisConnectionFactory redisConnectionFactory, UsageStreamSubscribe usageStreamSubscribe,
                                    StringRedisTemplate redisTemplate) {
        this.usageStreamSubscribe = usageStreamSubscribe;
        this.redisTemplate = redisTemplate;
        // 一次最多获取多少条消息
        // 运行 Stream 的 poll task
        // 可以理解为 Stream Key 的序列化方式
        // 可以理解为 Stream 后方的字段的 key 的序列化方式
        // 可以理解为 Stream 后方的字段的 value 的序列化方式
        // Stream 中没有消息时，阻塞多长时间，需要比 `spring.redis.timeout` 的时间小
        // 获取消息的过程或获取到消息给具体的消息者处理的过程中，发生了异常的处理
        // 将发送到Stream中的Record转换成ObjectRecord，转换成具体的类型是这个地方指定的类型
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, ObjectRecord<String, String>> options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                .builder()
                // 一次最多获取多少条消息
                .batchSize(100)
                // 运行 Stream 的 poll task
                .executor(Executors.newVirtualThreadPerTaskExecutor())
                // 可以理解为 Stream Key 的序列化方式
                .keySerializer(RedisSerializer.string())
                // 可以理解为 Stream 后方的字段的 key 的序列化方式
                .hashKeySerializer(RedisSerializer.string())
                // 可以理解为 Stream 后方的字段的 value 的序列化方式
                .hashValueSerializer(RedisSerializer.string())
                // Stream 中没有消息时，阻塞多长时间，需要比 `spring.redis.timeout` 的时间小
                .pollTimeout(Duration.ofSeconds(1))
                // 获取消息的过程或获取到消息给具体的消息者处理的过程中，发生了异常的处理
                .errorHandler(new StreamErrorHandler())
                // 将发送到Stream中的Record转换成ObjectRecord，转换成具体的类型是这个地方指定的类型
                .targetType(String.class)
                .build();
        this.streamMessageListenerContainer = StreamMessageListenerContainer.create(redisConnectionFactory, options);
    }
}
