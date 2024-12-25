package cn.com.farben.gptcoder.operation.platform.statistics.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/30
 * @author ltg
 * @apiNote 导出数据异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0004}
 *
 */
public final class ExportDataException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 3245897223748366959L;

    /**
     * 创建导出数据异常类
     * @param message 错误消息
     */
    public ExportDataException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0004, message);
    }
}
