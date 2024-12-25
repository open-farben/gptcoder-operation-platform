package cn.com.farben.gptcoder.operation.platform.dictionary.application.component;

import cn.com.farben.commons.errorcode.exception.OperationNotAllowedException;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁实现类
 * @author wuanhui
 **/
@Component
public class SingleLockComponent implements DistributedLockComponent {
    private static final Log logger = LogFactory.get();
    private final StringRedisTemplate stringRedisTemplate;

    private static final String domain = "coder_api_lock:";

    private static final String UUID_PREFIX = UUID.randomUUID().toString(true) + "-";

    public SingleLockComponent(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试获得分布式所
     * {@inheritDoc}
     */
    @Override
    public String lockWith(String key, long expire) {
        key = CharSequenceUtil.isNotBlank(key) ? key + ":" : "";
        String lockKey = domain + ":" +key;
        String threadId = UUID_PREFIX + Thread.currentThread().getId();
        // 获取锁
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, threadId, expire, TimeUnit.SECONDS);
        logger.info("添加分布式锁key:{},结果：{}", key, success);
        if (Boolean.TRUE.equals(success)) {
            return threadId;
        } else {
            throw new OperationNotAllowedException("获取全局锁失败");
        }
    }

    /**
     * 释放锁操作
     * {@inheritDoc}
     * @return
     */
    @Override
    public Boolean release(String key) {
        try {
            key = CharSequenceUtil.isNotBlank(key) ? key + ":" : "";
            String lockKey = domain + ":" +key;
            // 获取线程唯一标识
            String threadId = UUID_PREFIX + Thread.currentThread().getId();
            // 获取锁中的标识
            String id = stringRedisTemplate.opsForValue().get(lockKey);
            // 判断标示是否一致
            if(threadId.equals(id)) {
                // 释放锁
                logger.info("成功释放锁key:{}", key);
                stringRedisTemplate.delete(lockKey);
            }
            return true;
        } catch (Exception e) {
            logger.error("释放锁key:{}结束，释放结果:失败!", key, e);
            throw new OperationNotAllowedException("释放全局锁失败");
        }
    }

}
