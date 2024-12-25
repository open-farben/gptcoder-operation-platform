package cn.com.farben.gptcoder.operation.platform.user.command;

import cn.com.farben.gptcoder.operation.commons.user.command.BasePageCommand;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询用户列表命令
 * @author wuanhui
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserAccountListCommand extends BasePageCommand {

    /** 搜索关键字 */
    private String searchKey;

    /** 职务ID */
    private String duty;

    /** 组织架构ID */
    private String organization;

    /** 长组织架构ID */
    private String fullOrganization;

    /** 启用（0）、禁用（1） */
    private Integer status;

}
