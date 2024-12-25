package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 插件使用统计表实体
 * @author wuanhui
 */
@Data
@Table("plugin_usage_statistics")
public class PluginUsageStatisticsPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 账号 */
    private String account;

    /** 日期 */
    private LocalDate day;

    /** 累计活跃时长（ms） */
    private Long activeTime;

    /** 总代码补全率 */
    private BigDecimal totalCompletion;

    /** 总响应时间(ms) */
    private BigDecimal totalResponse;

    /** 总调用次数 */
    private Long totalCallNumber;

    /** 平均代码补全率 */
    private BigDecimal avgCompletion;

    /** 创建时间 */
    private LocalDateTime createTime;
}
