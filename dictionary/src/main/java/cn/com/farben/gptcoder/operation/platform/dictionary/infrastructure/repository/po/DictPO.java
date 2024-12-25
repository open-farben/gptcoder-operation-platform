package cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.util.Date;

/**
 * 系统字典表
 * @author wuanhui
 */
@Data
@Table("t_dict")
public class DictPO implements IPO {
    /** 记录ID */
    @Id(keyType = KeyType.None)
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
