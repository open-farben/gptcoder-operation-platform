package cn.com.farben.commons.web.handler;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.web.utils.StreamUtils;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.com.farben.commons.web.ResultData;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

/**
 * 创建时间: 2022/11/14
 * @author ltg
 * @apiNote 系统sql异常统一处理
 * <p>统一处理{@link SQLException}系统异常</p>
 *
 */
@RestControllerAdvice
@Order(value = 1)
public class GlobalSQLExceptionHandler {
    private static final Log logger = LogFactory.get();

    /**
     * 系统内部数据库错误
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = SQLException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultData<Void> handleDatabaseException(SQLException e) {
        logger.error("操作DB出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        String message = e.getMessage();
        if (message.contains("Duplicate entry")) {
            return new ResultData.Builder<Void>().error(ErrorCodeEnum.SYSTEM_ERROR_B0501).message("重复数据").build();
        }
        // 数据库原始消息包含字段信息等，可能比较敏感，不直接返给前端
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.SYSTEM_ERROR_B0500).message("数据存取异常").build();
    }

    // 异常可以有很多自定义的Exception
    // 异常也可以有乐观锁这些问题
}
