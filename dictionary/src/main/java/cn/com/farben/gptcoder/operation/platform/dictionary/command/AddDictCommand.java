package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import lombok.Data;

/**
 * 添加字典信息
 * @author wuanhui
 */
@Data
public class AddDictCommand {

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
}
