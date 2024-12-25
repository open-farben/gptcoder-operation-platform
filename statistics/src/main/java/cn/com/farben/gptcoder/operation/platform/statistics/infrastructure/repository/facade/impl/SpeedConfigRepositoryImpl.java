package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.impl;

import cn.com.farben.commons.ddd.assembler.CommonAssemblerUtil;
import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.SpeedConfigEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.enums.SystemConfigEnum;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.SpeedConfigRepository;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper.SpeedConfigMapper;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po.SpeedConfigPO;
import org.springframework.stereotype.Repository;

/**
 *
 * 基础速率配置仓储接口实现
 * @author wuanhui
 *
 */
@Repository
public class SpeedConfigRepositoryImpl implements SpeedConfigRepository {
    private final SpeedConfigMapper speedConfigMapper;

    public SpeedConfigRepositoryImpl(SpeedConfigMapper speedConfigMapper) {
        this.speedConfigMapper = speedConfigMapper;
    }

    /**
     * 添加配置
     * @param speedEntity 配置信息
     * @return 操作结果
     */
    @Override
    public boolean addConfig(SpeedConfigEntity speedEntity) {
        SpeedConfigPO config = new SpeedConfigPO();
        CommonAssemblerUtil.assemblerEntityToPO(speedEntity, config);
        return speedConfigMapper.insertSelectiveWithPk(config) > 0;
    }

    /**
     * 修改配置
     * @param speedEntity 配置信息
     * @return 操作结果
     */
    @Override
    public boolean editConfig(SpeedConfigEntity speedEntity) {
        SpeedConfigPO config = new SpeedConfigPO();
        CommonAssemblerUtil.assemblerEntityToPO(speedEntity, config);
        return speedConfigMapper.update(config) > 0;
    }

    /**
     * 查询默认配置
     */
    @Override
    public SpeedConfigEntity findConfig() {
        return speedConfigMapper.findSpeedConfig(SystemConfigEnum.SYSTEM_BASE_SPEED.getCode());
    }
}
