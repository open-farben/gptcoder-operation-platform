package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 更改插件用户状态命令
 */
@Validated
@Data
public class ChangePluginUserStatusCommand {
    /** 用户id */
    @NotBlank(message = "用户id不能为空")
    private String id;
}
