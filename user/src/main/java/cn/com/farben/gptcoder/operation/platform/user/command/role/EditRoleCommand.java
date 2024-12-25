package cn.com.farben.gptcoder.operation.platform.user.command.role;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改角色命令
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EditRoleCommand extends AddRoleCommand {

    /** 主键ID */
    private String id;
}
