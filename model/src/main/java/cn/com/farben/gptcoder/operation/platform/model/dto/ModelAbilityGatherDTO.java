package cn.com.farben.gptcoder.operation.platform.model.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.List;

/**
 *
 * 模型能力汇聚DTO<br>
 * 创建时间：2023/10/27<br>
 * @author ltg
 *
 */
@Data
public class ModelAbilityGatherDTO implements IDTO {
    /** 能力名称 */
    private String abilityName;

    /** 能力编码 */
    private String abilityCode;

    /** 描述 */
    private String describe;

    /** 模型参数详情 */
    private List<ModelParameterDetailDTO> parameterDetails;
}
