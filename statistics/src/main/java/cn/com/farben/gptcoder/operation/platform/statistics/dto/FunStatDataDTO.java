package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能方法使用人数统计查询返回db类
 * @author wuanhui
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FunStatDataDTO {

    /** 日期统计 */
    private String date;

    /** 统计数量 */
    private Integer value;

    /** 功能方法名称 */
    private String type;

    /** 模型能力代码 */
    private String funId;

    /** 模型能力枚举 */
    private ModelAbilityEnum modelAbility;
}
