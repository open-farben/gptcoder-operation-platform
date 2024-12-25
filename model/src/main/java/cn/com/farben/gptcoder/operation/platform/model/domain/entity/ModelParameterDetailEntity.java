package cn.com.farben.gptcoder.operation.platform.model.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

/**
 *
 * 模型参数详情实体<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Data
public class ModelParameterDetailEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 模型id */
    private String modelId;

    /** 参数范围id */
    private String paramRangeId;

    /** 参数默认值 */
    private String defaultValue;

    /** 参数当前值 */
    private String paramValue;
}
