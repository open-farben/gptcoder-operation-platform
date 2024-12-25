package cn.com.farben.gptcoder.operation.platform.user.command.role;

import lombok.Data;

/**
 * 查询系统菜单列表命令
 * @author wuanhui
 */
@Data
public class QueryMenuCommand {

    /** 菜单名称 */
    private String menuName;

    /** 状态：0正常 1停用 */
    private Integer status;
}
