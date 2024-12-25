package cn.com.farben.gptcoder.operation.platform.plugin.version.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/9/27
 * @author ltg
 * @apiNote 插件文件异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0700}
 *
 */
public final class PluginFileException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建插件文件异常类
     * @param message 错误消息
     */
    public PluginFileException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0700, message);
    }
}
