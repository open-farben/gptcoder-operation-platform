package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import lombok.Data;

/**
 * 修改字典映射信息参数
 * @author wuanhui
 */
@Data
public class EditDictCodeCommand {
    /** 主键ID */
    private String id;

    /** 子编码 */
    private String kindCode;

    /** 值 */
    private String kindValue;

    /** 序号 */
    private Integer orderNo;

    /** 描述 */
    private String mark;

    /** 启用状态，默认0启用 */
    private Byte disable;
}
