package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 修改登录密码参数
 */
@Validated
@Data
public class EditPasswordCommand {
    /** 原始密码 */
    @NotBlank(message = "账号不能为空")
    private String userId;
    /** 原始密码 */
    @NotBlank(message = "原始密码不能为空")
    private String password;

    /** 新密码 */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
