package cn.com.farben.gptcoder.operation.platform.user.command;


import lombok.Data;

/**
 * 主键ID参数对象
 * @author wuanhui
 */
@Data
public class MultipleIdCommand {

    /** 用户ID，多个以逗号分割 */
    private String ids;
}
