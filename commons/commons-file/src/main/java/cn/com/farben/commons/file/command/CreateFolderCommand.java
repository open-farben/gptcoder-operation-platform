package cn.com.farben.commons.file.command;

import cn.com.farben.commons.file.infrastructure.enums.FileUseTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 创建目录命令
 */
@Validated
@Data
public class CreateFolderCommand {
    /** 目录名 */
    @NotBlank(message = "目录名不能为空")
    private String folderName;

    /** 使用类型 */
    @NotNull(message = "使用类型不能为空")
    private FileUseTypeEnum useType;

    /** 上级目录 */
    private String parentPath;
}
