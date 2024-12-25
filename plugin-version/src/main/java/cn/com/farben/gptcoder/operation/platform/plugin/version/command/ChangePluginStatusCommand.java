package cn.com.farben.gptcoder.operation.platform.plugin.version.command;

import cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums.PluginStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 改变插件状态命令
 */
@Validated
@Data
public class ChangePluginStatusCommand {
    /** 记录ID */
    @NotBlank(message = "id不能为空")
    private String id;

    /** 插件状态 */
    @NotNull(message = "插件状态不能为空")
    private PluginStatusEnum status;
}
