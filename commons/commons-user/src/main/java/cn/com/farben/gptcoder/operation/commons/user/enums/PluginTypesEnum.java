package cn.com.farben.gptcoder.operation.commons.user.enums;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.hutool.core.text.CharSequenceUtil;
import com.mybatisflex.annotation.EnumValue;
import lombok.Getter;

import java.util.Objects;

/**
 * 插件类型枚举
 */
@Getter
public enum PluginTypesEnum {
    VSCODE("VSCode", "VSCode插件", "vsix"),
    JETBRAINS("JetBrains", "JetBrains插件", "zip");

    PluginTypesEnum(String type, String describe, String fileType) {
        this.type = type;
        this.describe = describe;
        this.fileType = fileType;
    }

    public void checkFileType(String fileType) {
        Objects.requireNonNull(fileType, "文件类型不能为空");
        if (!this.fileType.equals(fileType)) {
            throw new BusinessException(ErrorCodeEnum.USER_ERROR_A0700, CharSequenceUtil.format("{}文件类型只能为{}", describe, this.fileType));
        }
    }

    public static PluginTypesEnum convertType(String type) {
        for(PluginTypesEnum item : PluginTypesEnum.values()) {
            if(item.getType().equalsIgnoreCase(type) || item.name().equalsIgnoreCase(type)) {
                return item;
            }
        }
        return null;
    }

    @EnumValue
    private final String type;
    private final String describe;
    private final String fileType;
}
