package cn.com.farben.gptcoder.operation.platform.model.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/28
 * @author ltg
 * @apiNote 模型不存在异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0402}
 *
 */
public final class ModelNotExistsException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建模型不存在异常类
     */
    public ModelNotExistsException() {
        super(ErrorCodeEnum.USER_ERROR_A0402);
    }

    /**
     * 创建模型不存在异常类
     * @param message 错误消息
     */
    public ModelNotExistsException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0402, message);
    }
}
