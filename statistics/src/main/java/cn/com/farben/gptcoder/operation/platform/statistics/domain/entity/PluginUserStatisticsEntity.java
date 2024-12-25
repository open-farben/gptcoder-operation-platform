package cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums.ModelFeatureEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * 插件使用统计实体<br>
 * 创建时间：2023/9/12<br>
 * @author ltg
 *
 */
@Data
public class PluginUserStatisticsEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 用户账号 */
    private String account;

    /** 日期 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate day;

    /** 生成代码次数 */
    private Integer generationCount;

    /** 注释代码次数 */
    private Integer explainCount;

    /** 转换代码次数 */
    private Integer translateCount;

    /** 代码采纳次数 */
    private Integer acceptCount;

    /** 总代码补全率 */
    private BigDecimal totalCompletion;

    /** 生成代码行数 */
    private Integer generationLines;

    /** 代码采纳行数 */
    private Integer acceptLines;

    /** 总响应时间(ms) */
    private Integer totalResponse;

    /**
     * 功能ID，对应ModelFeatureEnum
     * @see ModelFeatureEnum
     */
    private String funId;

    /** 对应插件版本 */
    private String version;
}
