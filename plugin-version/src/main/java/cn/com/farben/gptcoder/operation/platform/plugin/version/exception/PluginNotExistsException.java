package cn.com.farben.gptcoder.operation.platform.plugin.version.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/10/07
 * @author ltg
 * @apiNote 插件不存在异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0400}
 *
 */
public final class PluginNotExistsException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建插件不存在异常类
     */
    public PluginNotExistsException() {
        super(ErrorCodeEnum.USER_ERROR_A0400);
    }

    /**
     * 创建插件不存在异常类
     * @param message 错误消息
     */
    public PluginNotExistsException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0400, message);
    }
}
