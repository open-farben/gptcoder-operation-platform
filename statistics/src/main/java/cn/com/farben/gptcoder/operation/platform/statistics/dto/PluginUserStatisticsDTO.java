package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * 插件用户使用情况统计结果DTO<br>
 * 创建时间：2023/8/28<br>
 * @author ltg
 *
 */
@Data
public class PluginUserStatisticsDTO implements IDTO {
    /** 序号 */
    private Integer sort;

    /** 用户账号 */
    private String account;

    /** 累计使用次数 */
    private Integer useCount;

    /** 生成代码次数 */
    private Integer generationCount;

    /** 注释代码次数 */
    private Integer explainCount;

    /** 转换代码次数 */
    private Integer translateCount;

    /** 平均代码补全率 */
    private BigDecimal avgCompletionRate;

    /** 平均代码补全率字符串 */
    private String avgCompletionRateStr;

    /** 生成代码行数 */
    private Integer generationLines;

    /** 代码采纳行数 */
    private Integer acceptLines;

    /** 平均响应速率(ms) */
    private Integer avgResponseRate;

    /** 最近使用时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastUsedTime;

    /** 插件版本 */
    private String version;

    /** 工号 */
    private String jobNumber;

    /** 姓名 */
    private String name;

    /** 组织架构名称 */
    private String organizationName;

    /** 职位名称 */
    private String dutyName;

    /** 职位ID */
    private String duty;

    /** 组织架构ID */
    private String organization;

}
