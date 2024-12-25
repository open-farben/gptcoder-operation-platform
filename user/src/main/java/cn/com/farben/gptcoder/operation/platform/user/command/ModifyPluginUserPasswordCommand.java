package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 插件用户修改密码命令
 */
@Validated
@Data
public class ModifyPluginUserPasswordCommand {
    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空")
    private String account;

    /** 原密码 */
    @NotBlank(message = "原密码不能为空")
    private String password;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
