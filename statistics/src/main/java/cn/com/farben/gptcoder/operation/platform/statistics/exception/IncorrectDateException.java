package cn.com.farben.gptcoder.operation.platform.statistics.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/15
 * @author ltg
 * @apiNote 日期不正确异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0414}
 *
 */
public final class IncorrectDateException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 3245897223748366959L;

    /**
     * 创建日期不正确异常类
     * @param message 错误消息
     */
    public IncorrectDateException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0414, message);
    }
}
