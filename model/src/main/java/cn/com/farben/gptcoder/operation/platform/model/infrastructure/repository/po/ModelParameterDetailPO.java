package cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

/**
 * 模型参数详情表
 */
@Data
@Table("model_parameter_detail")
public class ModelParameterDetailPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
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
