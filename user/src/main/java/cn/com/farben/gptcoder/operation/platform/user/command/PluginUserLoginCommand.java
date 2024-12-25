package cn.com.farben.gptcoder.operation.platform.user.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 插件用户登陆命令
 */
@Validated
@Data
public class PluginUserLoginCommand {
    /** 标识码，a80f740c-0779-4294-a700-2020f82769d4 */
    @NotBlank(message = "标识码不能为空")
    private String uuid;

    /** 机器id，7208f1a75ce6149a83fc4e752e32e569c87fc6309a455ef43945ec6bebbab7a5 */
    @NotBlank(message = "机器id不能为空")
    private String machineId;

    /** 设备，vscode_desktop */
    @NotBlank(message = "设备不能为空")
    private String device;

    /** 用户账号 */
    @NotBlank(message = "用户账号不能为空")
    private String userId;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    private String password;

    /** ide名称 */
    private String ideName;

    /** ide版本 */
    private String ideVersion;

    /** 插件类型 */
    private String pluginType;

    /** 插件版本 */
    private String pluginVersion;
}
