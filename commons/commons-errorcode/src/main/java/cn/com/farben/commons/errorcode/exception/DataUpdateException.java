package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 数据更新异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0504}
 *
 */
public final class DataUpdateException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 9145997223748467959L;

    /**
     * 创建数据更新异常类
     * @param message 错误消息
     */
    public DataUpdateException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0504, message);
    }
}
