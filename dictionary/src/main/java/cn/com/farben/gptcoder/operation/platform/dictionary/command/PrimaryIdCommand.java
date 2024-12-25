package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 根据主键查询的参数
 * @author wuanhui
 */
@Data
@Validated
public class PrimaryIdCommand {
    /** 主键ID */
    @NotBlank(message = "数据标识不能为空")
    private String id;
}
