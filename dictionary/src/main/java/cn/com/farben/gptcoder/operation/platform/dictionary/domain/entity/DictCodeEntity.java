package cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

import java.util.Date;

/**
 * 系统字典映射信息实体
 * @author wuanhui
 */
@Data
public class DictCodeEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 字典编码 */
    private String kindCode;

    /** 字典值 */
    private String kindValue;

    /** 所属码表 */
    private String dictCode;

    /** 所属父级码表 */
    private String parentCode;

    /** 序号 */
    private Integer orderNo;

    /** 描述 */
    private String mark;

    /** 启用状态，默认0启用 */
    private Byte disable;

    /** 0 正常 1删除 */
    private Byte logicDelete;

    /** 创建者 */
    private String createBy;

    /** 创建者 */
    private Date createDate;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private Date updateDate;
}
