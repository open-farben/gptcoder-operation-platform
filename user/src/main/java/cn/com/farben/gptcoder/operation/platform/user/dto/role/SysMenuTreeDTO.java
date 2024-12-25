package cn.com.farben.gptcoder.operation.platform.user.dto.role;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.Vector;

/**
 * 系统菜单树返回对象
 * @author wuanhui
 */
@Data
public class SysMenuTreeDTO implements IDTO {

    /** 菜单ID */
    private String id;

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID */
    private String parentId;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 当前登录人是否拥有该菜单权限，没权限则将字段设置为true */
    private boolean disabled;

    /** 子集架构 */
    Vector<SysMenuTreeDTO> children;

    public void addChildren(SysMenuTreeDTO sunMenu) {
        if(this.children == null) {
            this.children = new Vector<>();
        }
        this.children.add(sunMenu);
    }

}
