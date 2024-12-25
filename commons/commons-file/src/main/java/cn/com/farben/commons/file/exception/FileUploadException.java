package cn.com.farben.commons.file.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 文件上传异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0500}
 *
 */
public final class FileUploadException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建文件上传异常类
     * @param message 错误消息
     */
    public FileUploadException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0321, message);
    }
}
