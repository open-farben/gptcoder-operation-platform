package cn.com.farben.gptcoder.operation.platform.user.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/16
 * @author ltg
 * @apiNote 非法手机号异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0402}
 *
 */
public final class IllegalMobileException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建非法手机号异常类
     * @param message 错误消息
     */
    public IllegalMobileException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0402, message);
    }
}
