package cn.com.farben.gptcoder.operation.platform.dictionary.command;

import cn.com.farben.gptcoder.operation.commons.user.command.BasePageCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.validation.annotation.Validated;

/**
 * 查询系统字典明细配置
 * @author wuanhui
 */
@Data
@Validated
@EqualsAndHashCode(callSuper=false)
public class DictDetailCommand extends BasePageCommand {

    /** 字段配置ID */
    @NotBlank(message = "数据标识不能为空")
    private String id;
}
