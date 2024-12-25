package cn.com.farben.gptcoder.operation.platform.user.command.role;


import lombok.Data;

import java.util.List;

/**
 * 角色权限范围授权命令
 * @author wuanhui
 */
@Data
public class RoleRangeAuthCommand {

    /** 角色ID */
    private String roleId;

    /** 范围类型 */
    private Integer rangeType;

    /** 包含的机构编码列表 */
    private List<AuthOrganRangeCommand> organList;
}
