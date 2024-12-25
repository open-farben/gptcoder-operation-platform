package cn.com.farben.gptcoder.operation.platform.user.command.role;

import lombok.Data;

/**
 * 角色权限授权机构
 * @author wuanhui
 */
@Data
public class AuthOrganRangeCommand {

    /** 机构号 */
    private Integer organNo;

    /** 机构长编码 */
    private String fullOrganNo;
}
