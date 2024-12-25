package cn.com.farben.gptcoder.operation.platform.user.dto.role;

import cn.com.farben.commons.ddd.dto.IDTO;
import lombok.Data;

/**
 * 系统角色下拉框查询结果
 * @author wuanhui
 */
@Data
public class SysRoleTreeDTO implements IDTO {

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
}
