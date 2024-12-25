package cn.com.farben.gptcoder.authentication.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenCommand {
    /** 访问令牌 */
    @NotBlank(message = "访问令牌不能为空")
    private String accessToken;

    /** 刷新令牌 */
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}
