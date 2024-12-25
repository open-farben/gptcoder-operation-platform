package cn.com.farben.gptcoder.operation.platform.plugin.version.application.service;

import cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity.PluginVersionEntity;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.PluginConstants;
import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.PluginVersionRepository;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * 插件版本缓存信息
 * 创建时间：2023/9/27
 * @author wuanhui
 *
 */
@Component
public class PluginCacheService {

    private static final Log logger = LogFactory.get();

    private final RedisTemplate<String, String> redisTemplate;

    private final PluginVersionRepository pluginVersionRepository;


    public PluginCacheService(RedisTemplate<String, String> redisTemplate, PluginVersionRepository pluginVersionRepository) {
        this.redisTemplate = redisTemplate;
        this.pluginVersionRepository = pluginVersionRepository;
    }


    public void initPluginCache() {
        //存在key，先删掉
        if(Boolean.TRUE.equals(redisTemplate.hasKey(PluginConstants.PLUGIN_VERSION_CACHE_KEY))) {
            redisTemplate.delete(PluginConstants.PLUGIN_VERSION_CACHE_KEY);
        }
        List<PluginVersionEntity> entityList = pluginVersionRepository.listDownloadPlugins();
        for(PluginVersionEntity item : entityList) {
            redisTemplate.opsForList().leftPush(PluginConstants.PLUGIN_VERSION_CACHE_KEY, item.getType().getType() + "_" + item.getVersion());
        }
    }

    public void addPluginCache(String type, String version) {
        redisTemplate.opsForList().leftPush(PluginConstants.PLUGIN_VERSION_CACHE_KEY, type + "_" + version);
    }

    public void deletePluginCache(String type, String version) {
        redisTemplate.opsForList().remove(PluginConstants.PLUGIN_VERSION_CACHE_KEY, 0, type + "_" + version);
    }

    public String findPluginCache(String type, String version) {
        List<String> list = redisTemplate.opsForList().range(PluginConstants.PLUGIN_VERSION_CACHE_KEY, 0 , -1);
        logger.info("当前缓存信息：{}", JSONUtil.toJsonStr(list));
        if(list == null) {
            return "";
        }
        return list.stream().filter(e -> e.equals(type + "_" + version)).findAny().orElse("");
    }

}
