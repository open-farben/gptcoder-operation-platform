package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 修改管理用户命令
 * @author wuanhui
 */
@Validated
@Data
public class EditUserAccountCommand {
    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空")
    private String userId;

    /** 用户姓名 */
    private String userName;

    /** 邮箱 */
    private String email;

    /** 角色ID */
    private String roleId;

    /** 用户工号 */
    private String jobNumber;

    /** 联系手机号 */
    private String phone;

    /** 性别 */
    private String sex;

    /** 籍贯 */
    private String nativePlace;

    /** 联系地址 */
    private String address;

    /** 职位ID */
    private String positionId;

    /** 职位名称 */
    private String positionName;

    /** 组织架构ID */
    private String structId;

    /** 组织架构名称 */
    private String structName;

    /** 部门ID */
    private String departmentId;

    /** 部门名称 */
    private String departmentName;

    /** 个人联系邮箱 */
    private String personEmail;
}
