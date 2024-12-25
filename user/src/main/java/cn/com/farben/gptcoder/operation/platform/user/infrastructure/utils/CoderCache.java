package cn.com.farben.gptcoder.operation.platform.user.infrastructure.utils;

import cn.com.farben.gptcoder.operation.platform.user.dto.LoginUserCacheDTO;
import cn.hutool.jwt.JWTException;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存信息处理对象类，保存系统本地缓存相关数据
 * @author wuanhui
 * @version 1.0
 */
@Component
public class CoderCache {

    private static final Log logger = LogFactory.get();

    private static final String HS256_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCooUzuyLlEjXASVGc";

    @Value("${coder.token.expire-time: 1800}")
    private long expireTime;

    @Value("${coder.token.refresh-time: 7200}")
    private long refreshTime;

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisTemplate<String, Object> redisTemplate;


    public CoderCache(StringRedisTemplate stringRedisTemplate, RedisTemplate<String, Object> redisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 添加列表数据缓存
     * @param key 缓存key
     * @param values 保存的values
     * @param expireTime 过期时间，单位（秒）
     */
    public void addCacheData(String key, Object values, long expireTime) {
        redisTemplate.opsForValue().set(key, values, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取数据缓存
     * @param key 缓存key
     * @return 保存数据
     */
    public Object getCacheData(String key) {
       return redisTemplate.opsForValue().get(key);
    }

    /**
     * 根据访问token获取当前登录用户的刷新token
     * @param token 访问token
     * @return 刷新token
     */
    public String findRefreshToken(String token){
        LoginUserCacheDTO userToken = (LoginUserCacheDTO)getCacheData(token);
        if(userToken == null) {
            return "";
        }
        return userToken.getRefreshToken();
    }

    /**
     * 验证token是否合法
     * @param token token
     * @return true合法
     */
    public boolean validateToken(String token) {
        try {
            return JWTUtil.verify(token, HS256_KEY.getBytes());
        } catch (JWTException e) {
            return false;
        }
    }
}
