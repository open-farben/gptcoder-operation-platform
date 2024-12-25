package cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 响应速率配置表
 * @author wuanhui
 */
@Data
@Table("t_speed_config")
public class SpeedConfigPO implements IPO {
    /** 主键ID */
    @Id(keyType = KeyType.None)
    private String id;

    /** 配置编码 */
    private String configCode;

    /** 配置名称 */
    private String configName;

    /** 设置的速率 */
    private BigDecimal speed;

    /** 创建者 */
    private String createBy;

    /** 修改者 */
    private String updateBy;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Integer logicDelete;
}
