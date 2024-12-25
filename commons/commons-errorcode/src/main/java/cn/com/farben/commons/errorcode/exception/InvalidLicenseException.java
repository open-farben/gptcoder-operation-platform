package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/9/20
 * @author ltg
 * @apiNote 无效许可证异常类，，参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0603}
 *
 */
public final class InvalidLicenseException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 7825897100048766948L;

    /**
     * 创建无效许可证异常类
     */
    public InvalidLicenseException() {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0603, ErrorCodeEnum.SYSTEM_ERROR_B0603.getDescribe());
    }

    /**
     * 创建无效许可证异常类
     * @param message 错误消息
     */
    public InvalidLicenseException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0603, message);
    }
}
