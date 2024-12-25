package cn.com.farben.gptcoder.operation.platform.user.command.role;


import lombok.Data;

/**
 * 启用、禁用菜单参数对象
 * @author wuanhui
 */
@Data
public class DisableMenuCommand {

    /** 主键ID */
    private String id;

    /** 启用（0）、禁用标识（1） */
    private Byte status;
}
