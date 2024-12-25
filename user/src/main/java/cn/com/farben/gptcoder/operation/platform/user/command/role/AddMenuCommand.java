package cn.com.farben.gptcoder.operation.platform.user.command.role;

import lombok.Data;

/**
 * 新增系统菜单命令
 * @author wuanhui
 */
@Data
public class AddMenuCommand {

    /** 菜单名称 */
    private String menuName;

    /** 父菜单ID */
    private String parentId;

    /** 显示顺序 */
    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否为外链（0是 1否） */
    private Byte blank;

    /** 是否缓存（0缓存 1不缓存） */
    private Byte keepAlive;

    /** 菜单类型（M目录 C菜单 F按钮） */
    private String menuType;

    /** 菜单状态（0显示 1隐藏） */
    private Byte hidden;

    /** 菜单状态（0正常 1停用） */
    private Byte status;

    /** 权限标识 */
    private String permission;

    /** 菜单图标 */
    private String icon;

    /** 描述 */
    private String remark;
}
