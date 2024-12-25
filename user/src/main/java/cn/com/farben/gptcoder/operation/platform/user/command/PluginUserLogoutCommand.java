package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 插件用户登出命令
 */
@Validated
@Data
public class PluginUserLogoutCommand {
    /** 标识码，a80f740c-0779-4294-a700-2020f82769d4 */
    @NotBlank(message = "标识码不能为空")
    private String uuid;

    @NotBlank(message = "账号不能为空")
    private String userId;
}
