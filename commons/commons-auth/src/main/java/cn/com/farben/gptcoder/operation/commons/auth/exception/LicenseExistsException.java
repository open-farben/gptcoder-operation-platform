package cn.com.farben.gptcoder.operation.commons.auth.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/10/8
 * @author ltg
 * @apiNote 授权码已存在异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0400}
 *
 */
public final class LicenseExistsException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建授权码已存在异常类
     * @param message 错误消息
     */
    public LicenseExistsException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0400, message);
    }
}
