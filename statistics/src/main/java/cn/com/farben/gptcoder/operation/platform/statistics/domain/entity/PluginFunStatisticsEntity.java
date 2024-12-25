package cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 插件功能统计表实体
 * @author wuanhui
 */
@Data
public class PluginFunStatisticsEntity implements IEntity {

    /** 记录ID */
    private String id;

    /** 账号 */
    private String account;

    /** 日期 */
    private LocalDate day;

    /** 模型能力 */
    private ModelAbilityEnum modelAbilityEnum;

    /** 使用次数 */
    private Integer useCount;

    /** 代码产生行数 */
    private Integer generateLines;

    /** 代码采纳行数 */
    private Integer acceptLines;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 采纳次数 */
    private Integer acceptCount;

}
