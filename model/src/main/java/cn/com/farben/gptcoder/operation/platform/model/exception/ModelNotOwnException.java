package cn.com.farben.gptcoder.operation.platform.model.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/29
 * @author ltg
 * @apiNote 没有拥有模型异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0508}
 *
 */
public final class ModelNotOwnException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建没有拥有模型异常类
     */
    public ModelNotOwnException() {
        super(ErrorCodeEnum.USER_ERROR_A0508);
    }

    /**
     * 创建没有拥有模型异常类
     * @param message 错误消息
     */
    public ModelNotOwnException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0508, message);
    }
}
