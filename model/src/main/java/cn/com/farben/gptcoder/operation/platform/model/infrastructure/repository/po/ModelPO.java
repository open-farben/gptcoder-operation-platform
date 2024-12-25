package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ModelStatusEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 模型表
 */
@Data
@Table("model")
public class ModelPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 模型名称 */
    private String name;

    /** 是否拥有（0否：1是） */
    private Byte own;

    /** 模型状态 */
    private ModelStatusEnum status;
}
