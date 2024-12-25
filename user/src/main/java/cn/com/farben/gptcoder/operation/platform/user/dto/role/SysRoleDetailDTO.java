package cn.com.farben.gptcoder.operation.platform.user.dto.role;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

import java.util.List;

/**
 * 系统角色详情查询结果
 * @author wuanhui
 */
@Data
public class SysRoleDetailDTO implements IDTO {

    /** 角色ID */
    private String id;

    /** 角色名称 */
    private String roleName;

    /** 角色权限字符串 */
    private String roleKey;

    /** 显示顺序 */
    private Integer roleSort;

    /** 状态（0正常 1停用） */
    private Byte status;

    /** 描述 */
    private String remark;

    /** 角色类型 */
    private Integer roleType;

    /** 角色范围 */
    private Integer rangeScope;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private String createTime;

    /** 修改者 */
    private String updateBy;

    /** 修改时间 */
    private String updateTime;

    /** 包含的菜单列表 */
    private List<String> menuList;

    /** 权限范围包含的架构列表 */
    private List<Integer> organList;

    /** 权限范围包含的架构列表 */
    private List<String> fullOrganList;
}
