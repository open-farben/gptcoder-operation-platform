package cn.com.farben.gptcoder.operation.platform.model.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.List;

/**
 *
 * 模型返回前端DTO<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Data
public class ModelManagerDTO implements IDTO {
    /** 模型能力列表 */
    private List<ModelAbilityDTO> abilitys;

    /** 模型参数列表 */
    private List<ModelParameterTypeDTO> parameters;

    /** 模型信息列表 */
    private List<ModelInfoDTO> models;
}
