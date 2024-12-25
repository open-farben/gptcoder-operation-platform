package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import lombok.Getter;

import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 业务处理异常类，表示在处理业务逻辑时发生错误，错误码参见{@link ErrorCodeEnum}
 *
 */
@Getter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7134897190746766939L;
    protected final ErrorCodeEnum errorCodeEnum;
    protected final String businessMessage;

    public BusinessException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getDescribe());
        this.errorCodeEnum = errorCodeEnum;
        this.businessMessage = null;
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.businessMessage = message;
        this.errorCodeEnum = errorCodeEnum;
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, String message, Throwable cause) {
        super(message, cause);
        this.businessMessage = message;
        this.errorCodeEnum = errorCodeEnum;
    }

    public BusinessException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getDescribe(), cause);
        this.errorCodeEnum = errorCodeEnum;
        this.businessMessage = null;
    }
}
