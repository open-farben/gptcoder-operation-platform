package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 非法参数异常类，错误码为A类，用户引起的，参见{@link ErrorCodeEnum}
 *
 */
public final class IllegalParameterException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 7825897100048766948L;

    /**
     * 创建非法参数异常类
     * @param errorCodeEnum 错误码，参见{@link ErrorCodeEnum}
     */
    public IllegalParameterException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }

    /**
     * 创建非法参数异常类
     * @param message 提示消息
     */
    public IllegalParameterException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0400, message);
    }

    /**
     * 创建非法参数异常类
     * @param errorCodeEnum 错误码，参见{@link ErrorCodeEnum}
     * @param message 错误消息
     */
    public IllegalParameterException(ErrorCodeEnum errorCodeEnum, String message) {
        super(errorCodeEnum, message);
    }
}
