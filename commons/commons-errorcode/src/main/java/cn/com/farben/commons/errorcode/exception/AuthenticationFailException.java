package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/11/23
 * @author ltg
 * @apiNote 鉴权失败异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0400}
 *
 */
public final class AuthenticationFailException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 3245897223748366959L;

    /**
     * 创建鉴权失败异常类
     * @param message 错误消息
     */
    public AuthenticationFailException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0400, message);
    }
}
