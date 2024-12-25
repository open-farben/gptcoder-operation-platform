package cn.com.farben.gptcoder.operation.platform.user.command.role;

import cn.com.farben.gptcoder.operation.commons.user.command.BasePageCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询系统菜单列表命令
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryRoleListCommand extends BasePageCommand {

    /** 角色名称 */
    private String roleName;

    /** 状态：0正常 1停用 */
    private Integer status;
}
