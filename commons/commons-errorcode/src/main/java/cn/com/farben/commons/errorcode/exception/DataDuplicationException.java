package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 重复数据异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0501}
 *
 */
public final class DataDuplicationException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8145897212748466959L;

    /**
     * 创建重复数据异常类
     * @param message 错误消息
     */
    public DataDuplicationException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0501, message);
    }
}
