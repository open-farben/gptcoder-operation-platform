package cn.com.farben.gptcoder.operation.platform.model.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ModelStatusEnum;
import lombok.Data;

/**
 *
 * 模型实体<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Data
public class ModelEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 模型名称 */
    private String name;

    /** 是否拥有（0否：1是） */
    private Byte own;

    /** 模型状态 */
    private ModelStatusEnum status;
}
