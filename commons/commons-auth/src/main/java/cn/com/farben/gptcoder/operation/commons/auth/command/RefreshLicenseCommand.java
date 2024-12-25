package cn.com.farben.gptcoder.operation.commons.auth.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 更新license命令
 */
@Validated
@Data
public class RefreshLicenseCommand {
    /** 授权码字符串 */
    @NotBlank(message = "授权码不能为空")
    private String lic;
}
