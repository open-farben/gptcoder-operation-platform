package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 不允许的操作异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0507}
 *
 */
public final class OperationNotAllowedException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 3245897223748366959L;

    /**
     * 创建不允许的操作异常类
     * @param message 错误消息
     */
    public OperationNotAllowedException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0507, message);
    }
}
