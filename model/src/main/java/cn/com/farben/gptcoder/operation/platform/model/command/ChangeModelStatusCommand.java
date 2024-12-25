package cn.com.farben.gptcoder.operation.platform.model.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 改变模型状态命令
 */
@Validated
@Data
public class ChangeModelStatusCommand {
    @NotBlank(message = "模型id不能为空")
    private String id;
}
