package cn.com.farben.gptcoder.operation.platform.statistics.application.component;

import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.AdoptionInfoEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.RedisConstants;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.AdoptionInfoRepository;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.RedisStreamCommands;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.PendingMessage;
import org.springframework.data.redis.connection.stream.PendingMessages;
import org.springframework.data.redis.connection.stream.PendingMessagesSummary;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 订阅使用信息的Stream消息
 */
@Component
public class UsageStreamSubscribe implements StreamListener<String, ObjectRecord<String, String>> {
    private static final Log logger = LogFactory.get();

    /** 消费组中的消费者 */
    @Setter
    @Getter
    private String consumerName;

    private final StringRedisTemplate redisTemplate;

    private final AdoptionInfoRepository adoptionInfoRepository;

    private final ThreadPoolTaskScheduler syncScheduler = new ThreadPoolTaskScheduler();

    private final ThreadPoolTaskScheduler claimScheduler = new ThreadPoolTaskScheduler();

    /** 定时任务标识 */
    private final AtomicBoolean taskFlg = new AtomicBoolean(false);

    /** 数据处理标识 */
    private final AtomicBoolean commitFlg = new AtomicBoolean(false);

    private final Map<String, AdoptionInfoEntity> messageMap = new ConcurrentHashMap<>();

    private final TransactionTemplate transactionTemplate;

    @Override
    public void onMessage(ObjectRecord<String, String> message) {
        String msgId = message.getId().getValue();
        logger.info("stream[{}]消费者组[{}]中的消费者[{}]接收到数据，消息id：[{}]", message.getStream(), RedisConstants.STREAM_GROUP_KEY, consumerName, msgId);
        changeRecord2Entity(msgId, message.getValue());
    }

    public UsageStreamSubscribe(StringRedisTemplate redisTemplate,
                                AdoptionInfoRepository adoptionInfoRepository, TransactionTemplate transactionTemplate) {
        this.redisTemplate = redisTemplate;
        this.adoptionInfoRepository = adoptionInfoRepository;
        this.transactionTemplate = transactionTemplate;
    }

    private void storeData() {
        if (commitFlg.get()) {
            return;
        }
        logger.info("插件使用信息入库");
        synchronized (this) {
            commitFlg.set(true);
            AtomicBoolean errFlg = new AtomicBoolean(false);
            String[] messageIdArray = new String[Math.min(messageMap.size(), RedisConstants.BATCH_SIZE)];
            List<AdoptionInfoEntity> insertDatalist = new ArrayList<>();
            int index = 0;
            for (Map.Entry<String, AdoptionInfoEntity> entityEntry : messageMap.entrySet()) {
                insertDatalist.add(entityEntry.getValue());
                messageIdArray[index] = entityEntry.getKey();
                index++;
                if (index >= RedisConstants.BATCH_SIZE) {
                    break;
                }
            }
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                    try {
                        // 数据入库
                        adoptionInfoRepository.batchAddAdoptionInfo(insertDatalist);
                        StreamOperations<String, String, String> streamOperations = redisTemplate.opsForStream();
                        // 返回已消费
                        streamOperations.acknowledge(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY, messageIdArray);
                        // 消费完成删除
                        streamOperations.delete(RedisConstants.GPTCODER_USAGE_STREAM_KEY, messageIdArray);
                    } catch (Exception e) {
                        logger.error("更新统计数据失败", e);
                        status.setRollbackOnly();
                        errFlg.set(true);
                    }
                }
            });
            taskFlg.set(false);
            syncScheduler.destroy();
            if (!errFlg.get()) {
                for (String messageId : messageIdArray) {
                    messageMap.remove(messageId);
                }
            }
            commitFlg.set(false);
            OffsetDateTime odt = OffsetDateTime.now();
            claimScheduler.initialize();
            claimScheduler.schedule(this::dealPendingMessage, LocalDateTime.now().plusSeconds(2)
                    .toInstant(odt.getOffset()));
        }
    }

    /**
     * 处理未成功处理的消息
     */
    private synchronized void dealPendingMessage() {
        StreamOperations<String, String, String> streamOperations = redisTemplate.opsForStream();
        PendingMessagesSummary summary = streamOperations.pending(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY);
        if (Objects.isNull(summary)) {
            claimScheduler.destroy();
            return;
        }
        long totalPendingMessages = summary.getTotalPendingMessages();
        if (totalPendingMessages == 0) {
            claimScheduler.destroy();
            return;
        }
        // 每个消费者的pending消息数量
        Map<String, Long> pendingMessagesPerConsumer = summary.getPendingMessagesPerConsumer();
        for (Map.Entry<String, Long> entry : pendingMessagesPerConsumer.entrySet()) {
            handleConsumerPendingMessage(streamOperations, entry);
        }
        claimScheduler.destroy();
    }

    /**
     * 处理消费者Pending的消息
     * @param entry 消费者Pending消息
     */
    private void handleConsumerPendingMessage(StreamOperations<String, String, String> streamOperations, Map.Entry<String, Long> entry) {
        String consumer = entry.getKey();
        Long value = entry.getValue();
        // 消费者
        logger.info("消费者[{}]，一共有[{}]条pending消息", consumer, value);
        PendingMessages pendingMessages = streamOperations.pending(RedisConstants.GPTCODER_USAGE_STREAM_KEY,
                Consumer.from(RedisConstants.STREAM_GROUP_KEY, consumer), Range.closed("0", "+"), RedisConstants.CLAIM_PENDING_COUNT);
        if (pendingMessages.isEmpty()) {
            // 已被处理过
            return;
        }
        for (PendingMessage message : pendingMessages) {
            RecordId recordId = message.getId();
            String id = recordId.getValue();
            // 消息从消费组中获取，到此刻的时间
            Duration elapsedTimeSinceLastDelivery = message.getElapsedTimeSinceLastDelivery();
            if (elapsedTimeSinceLastDelivery.getSeconds() < RedisConstants.DELAY_TIME * RedisConstants.PENDING_TIMES) {
                return;
            }
            RedisStreamCommands.XClaimOptions xClaimOptions = RedisStreamCommands.XClaimOptions.minIdle(
                            Duration.ofSeconds(RedisConstants.DELAY_TIME * RedisConstants.PENDING_TIMES)).ids(recordId)
                    .idle(Duration.ofSeconds(RedisConstants.DELAY_TIME * RedisConstants.PENDING_TIMES));
            // 大于PENDING_TIMES倍时间还未处理的
            List<MapRecord<String, String, String>> recordList = streamOperations.claim(RedisConstants.GPTCODER_USAGE_STREAM_KEY, RedisConstants.STREAM_GROUP_KEY, consumerName,
                    xClaimOptions);
            if (CollUtil.isEmpty(recordList)) {
                recordList = streamOperations.range(RedisConstants.GPTCODER_USAGE_STREAM_KEY, Range.rightOpen(id, id));
            }
            if (CollUtil.isEmpty(recordList)) {
                logger.warn("消息[{}]无法获取到进行处理，如果不是有多个消费者，可能会丢失！", message);
                return;
            }
            MapRecord<String, String, String> mapRecord = recordList.getFirst();
            for (Map.Entry<String, String> subEntry : mapRecord.getValue().entrySet()) {
                changeRecord2Entity(mapRecord.getId().getValue(), subEntry.getValue());
            }
        }
    }

    private void changeRecord2Entity(String messageId, String dataStr) {
        JSONObject dataJo;
        try {
            dataJo = JSONUtil.parseObj(dataStr);
        } catch (Exception e) {
            logger.error("解析上报数据时产生异常：", e);
            logger.error("解析上报数据时异常，消息id：{}, 消息内容：{}", messageId, dataStr);
            if (!messageMap.isEmpty()) {
                startScheduler();
            }
            return;
        }

        AdoptionInfoEntity entity = new AdoptionInfoEntity();
        entity.setInfoId(IdUtil.objectId());
        entity.setUserId(dataJo.getStr("account"));
        entity.setPluginType(dataJo.getStr("pluginType"));
        entity.setPluginsVer(dataJo.getStr("pluginVersion"));
        entity.setGenNum(dataJo.getInt("line"));
        entity.setPromptNum(dataJo.getInt("promptCharacterCount", 0));
        entity.setConfirmNum(dataJo.getInt("confirmLineCount", 0));
        entity.setComplementRate(dataJo.getBigDecimal("complementRate"));
        entity.setModelName(dataJo.getStr("model"));
        entity.setFunId(dataJo.getStr("ability"));
        entity.setCostTime(dataJo.getInt("interval"));
        String data = dataJo.getStr("date");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(data, dateTimeFormatter);
        entity.setAccessDate(dateTime.toLocalDate());
        entity.setCreateDate(dateTime);

        if (Objects.isNull(entity.getComplementCharNum())) {
            entity.setComplementCharNum(0);
        }
        if(Objects.nonNull(entity.getPromptNum())) {
            //新增代码补全率
            int total = entity.getPromptNum() + entity.getComplementCharNum();
            if(total > 0) {
                entity.setComplementRate(new BigDecimal(entity.getComplementCharNum()).divide(new BigDecimal(total), 4, RoundingMode.HALF_DOWN));
            }
        }
        if (Objects.isNull(entity.getComplementRate())) {
            entity.setComplementRate(BigDecimal.ZERO);
        }

        messageMap.put(messageId, entity);

        if (messageMap.size() >= RedisConstants.BATCH_SIZE) {
            // 数据量达到上限，入库
            storeData();
        } else {
            // 未达到上限，判断是否有定时任务，没有的话，启动，有的话跳过
            startScheduler();
        }
    }

    private void startScheduler() {
        if (!taskFlg.get()) {
            synchronized (this) {
                if (!taskFlg.get()) {
                    OffsetDateTime odt = OffsetDateTime.now();
                    syncScheduler.initialize();
                    syncScheduler.schedule(this::storeData, LocalDateTime.now().plusSeconds(RedisConstants.DELAY_TIME)
                            .toInstant(odt.getOffset()));
                    taskFlg.set(true);
                }
            }
        }
    }
}
