package cn.com.farben.gptcoder.operation.platform.user.domain.entity;

import cn.com.farben.commons.ddd.domain.IEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 系统菜单与角色关联实体
 * @author wuanhui
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleMenuEntity implements IEntity {

    /** 角色ID */
    private String roleId;

    /** 菜单ID */
    private String menuId;
}
