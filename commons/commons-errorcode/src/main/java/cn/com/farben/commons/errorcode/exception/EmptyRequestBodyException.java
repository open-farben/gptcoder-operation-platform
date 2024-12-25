package cn.com.farben.commons.errorcode.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import lombok.Getter;
import java.io.Serial;

/**
 *
 * 创建时间: 2023/8/11
 * @author ltg
 * @apiNote 传入空的request body异常，错误码参见{@link ErrorCodeEnum}
 *
 */
@Getter
public class EmptyRequestBodyException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8134897190756766949L;
    private final String message;
    private final ErrorCodeEnum errorCodeEnum;

    public EmptyRequestBodyException(ErrorCodeEnum errorCodeEnum) {
        super(errorCodeEnum.getDescribe());
        this.message = null;
        this.errorCodeEnum = errorCodeEnum;
    }

    public EmptyRequestBodyException(ErrorCodeEnum errorCodeEnum, String message) {
        super(message);
        this.message = message;
        this.errorCodeEnum = errorCodeEnum;
    }

    public EmptyRequestBodyException(ErrorCodeEnum errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getDescribe(), cause);
        this.message = null;
        this.errorCodeEnum = errorCodeEnum;
    }
}
