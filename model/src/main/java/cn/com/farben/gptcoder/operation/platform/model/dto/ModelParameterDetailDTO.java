package cn.com.farben.gptcoder.operation.platform.model.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamContainEnum;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamTypeEnum;
import lombok.Data;

/**
 *
 * 模型参数详情返回前端DTO<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Data
public class ModelParameterDetailDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 参数名 */
    private String paramName;

    /** 参数描述 */
    private String paramDesc;

    /** 参数类型 */
    private ParamTypeEnum paramType;

    /** 模型功能 */
    private ModelAbilityEnum modelAbility;

    /** 允许最小值 */
    private String minValue;

    /** 最小值是否包含 */
    private ParamContainEnum minContain;

    /** 允许最大值 */
    private String maxValue;

    /** 最大值是否包含 */
    private ParamContainEnum maxContain;

    /** 参数默认值 */
    private String defaultValue;

    /** 参数当前值 */
    private String paramValue;
}
