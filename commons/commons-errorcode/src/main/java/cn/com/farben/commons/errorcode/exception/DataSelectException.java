package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 数据查询异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0503}
 *
 */
public final class DataSelectException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8742997324748067919L;

    /**
     * 创建数据查询异常类
     * @param message 错误消息
     */
    public DataSelectException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0503, message);
    }
}
