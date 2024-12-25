package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure;

/**
 * redis相关常量类
 */
public class RedisConstants {
    /** 1.8版本后新的stream名称 */
    public static final String GPTCODER_USAGE_STREAM_KEY = "gptcoder-usage-stream";

    // 消费组名称
    public static final String STREAM_GROUP_KEY = "gptcoder-stream-group";

    // 一次最大处理数据量
    public static final int BATCH_SIZE = 1000;

    // 延迟处理时间，秒
    public static final int DELAY_TIME = 5;

    // 一次获取pending状态的消息最大条数
    public static final int CLAIM_PENDING_COUNT = 300;

    // 大于处理时间多少倍，则从pending队列重新处理
    public static final long PENDING_TIMES = 5L;

    // 消费者活跃最大限度，2小时。
    public static final long CONSUMER_MAX_IDLE_TIME = 2 * 60 * 60 * 1000L;

    private RedisConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
