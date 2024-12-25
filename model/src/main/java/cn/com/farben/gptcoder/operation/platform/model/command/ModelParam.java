package cn.com.farben.gptcoder.operation.platform.model.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Validated
@Data
public class ModelParam {
    @NotBlank(message = "参数名id为空")
    private String paramId;

    @NotBlank(message = "参数值不能为空")
    private String paramValue;
}
