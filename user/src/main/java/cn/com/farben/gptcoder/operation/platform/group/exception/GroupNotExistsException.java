package cn.com.farben.gptcoder.operation.platform.group.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * @apiNote 工作组不存在异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0201}
 *
 */
public final class GroupNotExistsException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建工作组不存在异常类
     */
    public GroupNotExistsException() {
        super(ErrorCodeEnum.USER_ERROR_A0201);
    }

    /**
     * 创建工作组不存在异常类
     * @param message 错误消息
     */
    public GroupNotExistsException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0201, message);
    }
}
