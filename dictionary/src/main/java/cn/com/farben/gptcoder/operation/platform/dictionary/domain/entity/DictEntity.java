package cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.Data;

/**
 * 系统字典信息实体
 * @author wuanhui
 */
@Data
public class DictEntity implements IEntity {
    /** 记录ID */
    private String id;

    /** 字典编码 */
    private String dictCode;

    /** 字典名称 */
    private String dictName;

    /** 字典值 */
    private String dictValue;

    /** 父级编码 */
    private String parentCode;

    /** 序号 */
    private Integer orderNo;

    /** 层级：0表示根字典 */
    private Byte dictLevel;

    /** 字典类型：0 常量  1级联 */
    private Byte dictType;

    /** 描述 */
    private String mark;

    /** 启用状态，默认0启用 */
    private Byte disable;

    /** 0 正常 1删除 */
    private int logicDelete;

    /** 创建者 */
    private String createBy;

    /** 更新者 */
    private String updateBy;
}
