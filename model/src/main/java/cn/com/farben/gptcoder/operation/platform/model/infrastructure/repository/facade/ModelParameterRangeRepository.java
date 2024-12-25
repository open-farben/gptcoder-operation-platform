package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade;

import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.model.domain.entity.ModelParameterRangeEntity;

import java.util.List;

/**
 *
 * 模型参数范围仓储接口<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
public interface ModelParameterRangeRepository {
    /**
     * 查询模型的所有能力
     * @param modelId 模型id
     * @return 能力列表
     */
    List<ModelAbilityEnum> listModelAbilitys(String modelId);

    /**
     * 根据参数详情id获取参数范围
     * @param detailId 参数详情id
     * @return 参数范围实体
     */
    ModelParameterRangeEntity getRangeByDetailId(String detailId);
}
