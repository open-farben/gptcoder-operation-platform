package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.mybatisflex.annotation.Table;
import lombok.Data;


/**
 * 系统角色菜单关联表
 * @author wuanhui
 */
@Data
@Table("sys_role_menu")
public class SysRoleMenuPO implements IPO {

    /** 角色ID */
    private String roleId;

    /** 菜单ID */
    private String menuId;

}
