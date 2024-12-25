package cn.com.farben.gptcoder.operation.platform.plugin.version.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 改变插件版本命令
 */
@Validated
@Data
public class ModifyPluginVersionCommand {
    /** 记录ID */
    @NotBlank(message = "id不能为空")
    private String id;

    /** 版本描述 */
    private String description;

    /** 支持的IDE版本 */
    @NotBlank(message = "支持的IDE版本不能为空")
    private String ideVersion;
}
