package cn.com.farben.gptcoder.operation.commons.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 当缓存用的系统角色信息
 * @author wuanhui
 */
@Data
public class SysRoleCacheDTO implements Serializable {

    /** 角色ID */
    private String id;

    /** 角色名称 */
    private String roleName;

    /** 角色权限字符串 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限） */
    private String dataScope;

    /** 菜单树选择项是否关联显示: 0是  1否 */
    private byte menuCheck;

    /** 部门树选择项是否关联显示: 0是  1否 */
    private byte deptCheck;

    /** 状态（0正常 1停用） */
    private Byte status;

    /** 描述 */
    private String remark;

    /** 角色类型 */
    private Integer roleType;

    /** 角色范围 */
    private Integer rangeScope;

}
