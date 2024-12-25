package cn.com.farben.gptcoder.operation.platform.model.command;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 修改模型参数命令
 */
@Validated
@Data
public class ChangeModelParamCommand {
    @NotBlank(message = "模型id不能为空")
    private String id;

    @NotNull(message = "模型参数不能为空")
    @NotEmpty(message = "模型参数不能为空")
    @Valid
    private List<ModelParam> params;
}
