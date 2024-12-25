package cn.com.farben.gptcoder.operation.platform.user.command;

import cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums.PluginUserStatusEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 新增插件用户命令
 */
@Validated
@Data
public class AddPluginUserCommand {
    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空")
    @Size(min = 3, max = 18, message = "用户账号为3-18个字符")
    private String account;

    /** 用户姓名 */
    @NotBlank(message = "用户姓名不能为空")
    @Size(min = 2, max = 20, message = "用户姓名为2-20个字符")
    private String name;

    /** 工号 */
    @Size( max = 20, message = "工号最多为20个字符")
    private String jobNumber;

    /** 职务 */
    @Size( max = 20, message = "职务最多为20个字符")
    private String duty;

    /** 所属组织 */
    @NotBlank(message = "机构号不能为空")
    @Size(max = 20, message = "机构号最多为20个字符")
    private String organization;

    /** 所属组织 */
    @NotBlank(message = "机构长架构不能为空")
    private String fullOrganization;

    /** 手机号 */
//    @Pattern(regexp="^1[3|4|5|6|7|8][0-9]\\d{8}$", message="手机号不正确")
    private String mobile;

    /** 邮箱 */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱不正确")
    @Size( max = 50, message = "邮箱最多为50个字符")
    private String email;

    /** 插件用户状态 */
    private PluginUserStatusEnum status;
}
