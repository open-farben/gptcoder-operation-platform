package cn.com.farben.gptcoder.operation.platform.model.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamContainEnum;
import lombok.Data;

/**
 *
 * 模型参数实体<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Data
public class ModelParameterRangeEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 参数id */
    private String parameterId;

    /** 模型能力 */
    private ModelAbilityEnum modelAbility;

    /** 允许最小值 */
    private String minValue;

    /** 最小值是否包含 */
    private ParamContainEnum minContain;

    /** 允许最大值 */
    private String maxValue;

    /** 最大值是否包含 */
    private ParamContainEnum maxContain;
}
