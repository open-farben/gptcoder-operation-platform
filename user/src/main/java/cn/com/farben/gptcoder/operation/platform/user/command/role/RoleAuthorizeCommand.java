package cn.com.farben.gptcoder.operation.platform.user.command.role;


import lombok.Data;

import java.util.List;

/**
 * 系统角色菜单授权命令
 * @author wuanhui
 */
@Data
public class RoleAuthorizeCommand {

    /** 角色ID */
    private String roleId;

    /** 包含的菜单列表 */
    private List<String> menuList;
}
