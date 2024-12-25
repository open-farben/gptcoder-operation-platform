package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/25
 * @author ltg
 * @apiNote 长度不正确异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0420}
 *
 */
public final class IncorrectLengthException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建长度不正确异常类
     */
    public IncorrectLengthException() {
        super(ErrorCodeEnum.USER_ERROR_A0420, ErrorCodeEnum.USER_ERROR_A0420.getDescribe());
    }

    /**
     * 创建长度不正确异常类
     * @param message 错误消息
     */
    public IncorrectLengthException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0420, message);
    }
}
