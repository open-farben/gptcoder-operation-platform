package cn.com.farben.gptcoder.operation.platform.plugin.version.command;

import cn.com.farben.gptcoder.operation.commons.user.enums.PluginTypesEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 新增版本命令
 */
@Validated
@Data
public class AddVersionCommand {
    /** 版本号 */
    @NotBlank(message = "版本号不能为空")
    private String version;

    /** 版本描述 */
    private String description;

    /** 插件类型 */
    @NotNull(message = "插件类型不能为空")
    private PluginTypesEnum type;

    /** redis的key */
    @NotBlank(message = "分析结果key不能为空")
    private String analysisKey;

    /** 是否发布 */
    @NotNull(message = "是否发布不能为空")
    private Boolean publish;

    /** 支持的IDE版本 */
    @NotBlank(message = "支持的IDE版本不能为空")
    private String ideVersion;
}
