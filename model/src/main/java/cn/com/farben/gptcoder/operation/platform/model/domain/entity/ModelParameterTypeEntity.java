package cn.com.farben.gptcoder.operation.platform.model.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamTypeEnum;
import lombok.Data;

/**
 *
 * 模型参数类型实体<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Data
public class ModelParameterTypeEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 参数名 */
    private String paramName;

    /** 参数描述 */
    private String paramDesc;

    /** 参数类型 */
    private ParamTypeEnum paramType;
}