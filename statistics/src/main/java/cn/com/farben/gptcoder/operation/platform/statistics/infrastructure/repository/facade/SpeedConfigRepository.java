package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.platform.statistics.domain.entity.SpeedConfigEntity;

/**
 * 基础速率配置仓储接口
 * @author wuanhui
 *
 */
public interface SpeedConfigRepository {

    /**
     * 添加配置
     * @param speedEntity 配置信息
     * @return 操作结果
     */
    boolean addConfig(SpeedConfigEntity speedEntity);

    /**
     * 修改配置
     * @param speedEntity 配置信息
     * @return 操作结果
     */
    boolean editConfig(SpeedConfigEntity speedEntity);

    /**
     * 查询默认配置
     */
    SpeedConfigEntity findConfig();
}
