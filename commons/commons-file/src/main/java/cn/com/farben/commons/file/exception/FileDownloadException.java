package cn.com.farben.commons.file.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * 文件下载异常类，错误码参见{@link ErrorCodeEnum#SYSTEM_ERROR_B0321}
 *
 */
public final class FileDownloadException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建文件下载异常类
     * @param message 错误消息
     */
    public FileDownloadException(String message) {
        super(ErrorCodeEnum.SYSTEM_ERROR_B0321, message);
    }
}
