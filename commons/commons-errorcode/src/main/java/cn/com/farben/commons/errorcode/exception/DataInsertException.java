package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 新增数据异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0505}
 *
 */
public final class DataInsertException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建新增数据异常类
     * @param message 错误消息
     */
    public DataInsertException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0505, message);
    }
}
