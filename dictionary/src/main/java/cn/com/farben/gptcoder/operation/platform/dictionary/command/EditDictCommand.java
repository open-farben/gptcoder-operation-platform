package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import lombok.Data;

/**
 * 修改字典信息
 * @author wuanhui
 */
@Data
public class EditDictCommand {
    /** 记录ID */
    private String id;

    /** 字典名称 */
    private String dictName;

    /** 序号 */
    private Integer orderNo;

    /** 描述 */
    private String mark;
}
