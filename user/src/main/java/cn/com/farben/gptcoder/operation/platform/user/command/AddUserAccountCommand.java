package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 新增用户命令
 */
@Validated
@Data
public class AddUserAccountCommand {

    private String id;

    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空")
    private String userId;

    /** 用户姓名 */
    @NotBlank(message = "用户姓名不能为空")
    private String userName;

    /** 邮箱 */
    @NotBlank(message = "邮箱地址不能为空")
    private String email;

    /** 角色ID */
    private String roleId;

    /** 用户工号 */
    private String jobNumber;

    /** 联系手机号 */
    private String phone;

    /** 职位ID */
    private String duty;

    /** 组织架构ID */
    private Integer organization;

    /** 组织架构长编码Code */
    private String fullOrganization;

    /** 状态（0 启用，1 禁用） */
    private Integer inUse;


}
