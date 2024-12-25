package cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 插件使用统计表实体
 * @author wuanhui
 */
@Data
public class PluginUsageStatisticsEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 账号 */
    private String account;

    /** 日期 */
    private LocalDate day;

    /** 累计活跃时长（s） */
    private Long activeTime;

    /** 总代码补全率 */
    private BigDecimal totalCompletion;

    /** 总响应时间(ms) */
    private Long totalResponse;

    /** 总调用次数 */
    private Long totalCallNumber;

    /** 平均代码补全率 */
    private BigDecimal avgCompletion;

    /** 创建时间 */
    private LocalDateTime createTime;
}
