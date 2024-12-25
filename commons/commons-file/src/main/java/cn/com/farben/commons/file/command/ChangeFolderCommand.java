package cn.com.farben.commons.file.command;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 修改目录名命令
 */
@Validated
@Data
public class ChangeFolderCommand {
    /** id */
    @NotBlank(message = "目录id不能为空")
    private String id;

    /** 目录名 */
    @NotBlank(message = "目录名不能为空")
    private String name;
}
