package cn.com.farben.gptcoder.operation.platform.statistics.dto;

import cn.com.farben.commons.ddd.dto.IDTO;
import cn.hutool.core.annotation.Alias;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * 用户使用插件情况统计结果DTO<br>
 * 创建时间：2023/11/1<br>
 * @author ltg
 *
 */
@Data
public class PluginUsageStatisticsDTO implements IDTO {
    /** 序号 */
    @Alias(value = "序号")
    private Integer sort;

    /** 用户账号 */
    @Alias(value = "账号")
    private String account;

    /** 工号 */
    @Alias(value = "工号")
    private String jobNumber;

    /** 姓名 */
    @Alias(value = "姓名")
    private String name;

    /** 组织架构名称 */
    @Alias(value = "组织机构")
    private String organizationName;

    /** 职位名称 */
    @Alias(value = "职位")
    private String dutyName;

    /** 累计活跃时长（min） */
    @Alias(value = "累计活跃时长（min）")
    private BigDecimal activeTime;

    /** 平均代码补全率 */
    @Alias(value = "平均代码补全率")
    private BigDecimal avgCompletionRate;

    /** 生成代码行数 */
    @Alias(value = "生成代码行数")
    private Integer generationLines;

    /** 采纳代码行数 */
    @Alias(value = "采纳代码行数")
    private Integer acceptLines;

    /** 平均响应速率(ms) */
    @Alias(value = "平均响应速率(ms)")
    private Integer avgResponseRate;

    /** 当前使用模型 */
    @Alias(value = "当前使用模型")
    private String currentModel;

    /** 当前使用插件 */
    @Alias(value = "当前使用插件")
    private String currentPlugin;

    /** 插件版本 */
    @Alias(value = "插件版本")
    private String currentPluginVersion;

    /** 最近使用时间 */
    @Alias(value = "最近使用时间")
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastUsedTime;
}
