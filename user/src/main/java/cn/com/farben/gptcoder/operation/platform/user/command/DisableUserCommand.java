package cn.com.farben.gptcoder.operation.platform.user.command;


import lombok.Data;

/**
 * 启用、禁用参数对象
 * @author wuanhui
 */
@Data
public class DisableUserCommand {

    /** 主键ID */
    private String id;

    /** 启用（0）、禁用标识（1） */
    private Byte status;
}
