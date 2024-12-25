package cn.com.farben.commons.ddd.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 空仓储层接口异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0002}
 *
 */
public final class NullRepositoryException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 7125897201748366959L;

    /**
     * 创建空仓储层接口异常类
     * @param message 错误消息
     */
    public NullRepositoryException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0002, message);
    }
}
