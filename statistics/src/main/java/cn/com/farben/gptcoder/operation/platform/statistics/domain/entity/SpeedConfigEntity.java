package cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 基础响应速率配置实体
 * @author wuanhui
 */
@Data
public class SpeedConfigEntity implements IEntity {

    /** 主键ID */
    private String id;

    /** 配置编码 */
    private String configCode;

    /** 配置名称 */
    private String configName;

    /** 设置的速率 */
    private BigDecimal speed;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private Date createDate;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    private Date updateDate;

    /** 逻辑删除标记 0 正常  1 删除 */
    private Integer logicDelete;
}
