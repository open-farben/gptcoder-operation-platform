package cn.com.farben.gptcoder.operation.platform.user.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/15
 * @author ltg
 * @apiNote 登陆异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0200}
 *
 */
public final class UserLoginException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建登陆异常类
     * @param message 错误消息
     */
    public UserLoginException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0200, message);
    }
    public UserLoginException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum);
    }
    public UserLoginException(ErrorCodeEnum errorCodeEnum,String message) {
        super(errorCodeEnum,message);
    }
}
