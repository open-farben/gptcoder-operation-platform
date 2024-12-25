package cn.com.farben.gptcoder.operation.commons.user.dto;

import com.mybatisflex.core.paginate.Page;
import lombok.Data;

/**
 * 系统字典配置明细及包含码表返回对象
 * @author wuanhui
 */
@Data
public class SystemDictInfoDTO {

    /** 记录ID */
    private String id;

    /** 字典编码 */
    private String dictCode;

    /** 字典名称 */
    private String dictName;

    /** 层级：0表示根字典 */
    private Byte dictLevel;

    /** 字典类型：0 常量  1级联 */
    private Byte dictType;

    /** 描述 */
    private String mark;

    /** 启用状态，默认0启用 */
    private Byte disable;

    /** 包含的码表信息 */
    private Page<DictCodeListDTO> dictList;
}
