package cn.com.farben.gptcoder.operation.platform.statistics.domain;

import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.SpeedConfigEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.enums.SystemConfigEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.SpeedConfigRepository;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import lombok.Getter;

import java.security.SecureRandom;

/**
 *
 * 插件使用记录聚合根<br>
 * 创建时间：2023/8/15<br>
 * @author ltg
 *
 */
@Getter
public class SpeedConfigAggregateRoot {

    private static final Log logger = LogFactory.get();

    private final SpeedConfigRepository configRepository;

    /**
     * 新增配置
     * @param config 配置信息
     * @return true or false
     */
    public boolean addConfig(SpeedConfigEntity config) {
        SecureRandom random = new SecureRandom();
        Snowflake snowflake = IdUtil.getSnowflake(random.nextInt(32), random.nextInt(32));
        config.setId(snowflake.nextIdStr());
        config.setConfigCode(SystemConfigEnum.SYSTEM_BASE_SPEED.getCode());
        config.setConfigName(SystemConfigEnum.SYSTEM_BASE_SPEED.getDesc());
        config.setCreateBy("admin");
        return configRepository.addConfig(config);
    }

    /**
     * 修改配置
     * @param config 配置信息
     * @return true or false
     */
    public boolean editConfig(SpeedConfigEntity config) {
        config.setUpdateBy("admin");
        return configRepository.editConfig(config);
    }

    public SpeedConfigEntity findConfig() {
        return configRepository.findConfig();
    }

    public static class Builder {
        /** 插件使用记录仓储接口 */
        private final SpeedConfigRepository configRepository;

        public Builder(SpeedConfigRepository configRepository) {
            this.configRepository = configRepository;
        }

        public SpeedConfigAggregateRoot build() {
            return new SpeedConfigAggregateRoot(this);
        }
    }

    private SpeedConfigAggregateRoot(SpeedConfigAggregateRoot.Builder builder) {
        this.configRepository = builder.configRepository;
    }
}
