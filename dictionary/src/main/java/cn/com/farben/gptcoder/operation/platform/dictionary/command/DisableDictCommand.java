package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 启用、禁用字典配置
 * @author wuanhui
 */
@Data
@Validated
public class DisableDictCommand {
    /** 字典ID */
    @NotBlank(message = "数据标识不能为空")
    private String id;

    /** 启用状态: 0启用   1禁用 */
    @NotNull(message = "操作标识不能为空")
    private Byte disable;
}
