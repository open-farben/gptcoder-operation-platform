package cn.com.farben.gptcoder.authentication.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class GenerateTokenCommand {
    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String userName;

    /** 用户所属角色 */
    @NotEmpty(message = "用户角色不能为空")
    private List<String> roles;
}
