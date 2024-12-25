package cn.com.farben.gptcoder.operation.platform.group.exception;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;

import java.io.Serial;

/**
 *
 * @apiNote 工作组已存在异常类，错误码参见{@link ErrorCodeEnum#USER_ERROR_A0111}
 *
 */
public final class GroupExistsException extends BusinessException {
    @Serial
    private static final long serialVersionUID = 8245997323748567919L;

    /**
     * 创建工作组已存在异常类
     * @param message 错误消息
     */
    public GroupExistsException(String message) {
        super(ErrorCodeEnum.USER_ERROR_A0111, message);
    }
}
