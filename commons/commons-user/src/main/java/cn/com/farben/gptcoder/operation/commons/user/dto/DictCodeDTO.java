package cn.com.farben.gptcoder.operation.commons.user.dto;

import lombok.Data;

/**
 * 系统字典配置映射返回对象
 * @author wuanhui
 */
@Data
public class DictCodeDTO {

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


}
