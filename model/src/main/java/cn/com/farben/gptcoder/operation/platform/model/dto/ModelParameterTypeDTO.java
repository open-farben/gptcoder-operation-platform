package cn.com.farben.gptcoder.operation.platform.model.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ParamTypeEnum;
import lombok.Data;

/**
 *
 * 模型参数类型返回前端DTO<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Data
public class ModelParameterTypeDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 参数名 */
    private String paramName;

    /** 参数描述 */
    private String paramDesc;

    /** 参数类型 */
    private ParamTypeEnum paramType;
}
