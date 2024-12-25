package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import cn.com.farben.gptcoder.operation.commons.model.enums.ModelAbilityEnum;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 插件功能统计表实体
 * @author wuanhui
 */
@Data
@Table("plugin_function_statistics")
public class PluginFunctionStatisticsPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 账号 */
    private String account;

    /** 日期 */
    private LocalDate day;

    /** 模型能力 */
    private ModelAbilityEnum modelAbility;

    /** 使用次数 */
    private Integer useCount;

    /** 采纳次数 */
    private Integer acceptCount;

    /** 代码产生行数 */
    private Integer generateLines;

    /** 代码采纳行数 */
    private Integer acceptLines;

    /** 创建时间 */
    private LocalDateTime createTime;

}
