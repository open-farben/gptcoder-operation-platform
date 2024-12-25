package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 字典主键ID配置
 * @author wuanhui
 */
@Data
@Validated
public class DictIdCommand {

    /** 用户ID，多个以逗号分割 */
    @NotBlank(message = "选择标识不能为空")
    private String ids;
}
