package cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;

import cn.com.farben.commons.ddd.po.IPO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.time.LocalDateTime;


/**
 * 系统菜单表
 * @author wuanhui
 */
@Data
@Table("sys_menu")
public class SysMenuPO implements IPO {

    /** 菜单ID */
    @Id(keyType = KeyType.None)
    private String id;

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

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
    /** 描述 */
    private String remark;
}
