package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamContainEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 模型参数范围表
 */
@Data
@Table("model_parameter_range")
public class ModelParameterRangePO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
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
