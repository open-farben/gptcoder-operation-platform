package cn.com.farben.gptcoder.operation.platform.model.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums.ModelStatusEnum;
import lombok.Data;

import java.util.List;

/**
 *
 * 模型信息返回前端DTO<br>
 * 创建时间：2023/10/25<br>
 * @author ltg
 *
 */
@Data
public class ModelInfoDTO implements IDTO {
    /** 记录ID */
    private String id;

    /** 模型名称 */
    private String name;

    /** 是否拥有（0否：1是） */
    private Byte own;

    /** 模型状态 */
    private ModelStatusEnum status;

    /** 模型能力列表 */
    private List<ModelAbilityGatherDTO> modelAbilitys;
}
