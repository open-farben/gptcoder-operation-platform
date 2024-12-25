package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamTypeEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 模型参数类型表
 */
@Data
@Table("model_parameter_type")
public class ModelParameterTypePO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 参数名 */
    private String paramName;

    /** 参数描述 */
    private String paramDesc;

    /** 参数类型 */
    private ParamTypeEnum paramType;
}
