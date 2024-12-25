package cn.com.farben.gptcoder.operation.platform.plugin.version.command;

import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 检查版本是否可用
 */
@Validated
@Data
public class CheckdVersionCommand {
    /** 版本号 */
    @NotBlank(message = "版本号不能为空")
    private String version;

    /** 插件类型 */
    @NotNull(message = "插件类型不能为空")
    private PluginTypesEnum type;
}
