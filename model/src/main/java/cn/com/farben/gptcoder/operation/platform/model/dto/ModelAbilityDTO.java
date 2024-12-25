package cn.com.farben.gptcoder.operation.platform.model.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 *
 * 模型能力返回前端DTO<br>
 * 创建时间：2023/10/24<br>
 * @author ltg
 *
 */
@Data
public class ModelAbilityDTO implements IDTO {
    /** 能力名称 */
    private String abilityName;

    /** 能力编码 */
    private String abilityCode;

    /** 描述 */
    private String describe;
}
