package cn.com.farben.gptcoder.operation.platform.user.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/9/21
 * @author ltg
 * @apiNote 用户状态异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A1006}
 *
 */
public final class UserStatusException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建用户状态异常类
     */
    public UserStatusException() {
        super(ErrorCodeEnum.USER_ERROR_A1006);
    }

    /**
     * 创建用户状态异常类
     * @param message 错误消息
     */
    public UserStatusException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A1006, message);
    }
}
