package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 用户登陆命令
 */
@Validated
@Data
public class UserLoginCommand {
    /** 用户ID */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** 验证码请求ID */
    private String verifyId;

    /** 验证码 */
    private String verifyCode;
}
