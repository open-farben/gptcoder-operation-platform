package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 修改登录密码参数
 */
@Validated
@Data
public class ResetPasswordCommand {

    /** 用户id */
    @NotBlank(message = "用户ID不能为空")
    private String id;
}
