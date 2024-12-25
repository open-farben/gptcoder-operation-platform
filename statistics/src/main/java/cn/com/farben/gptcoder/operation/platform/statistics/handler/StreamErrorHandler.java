package cn.com.farben.gptcoder.operation.platform.statistics.handler;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.ErrorHandler;

public class StreamErrorHandler implements ErrorHandler {

    private static final Log logger = LogFactory.get();
    @Override
    public void handleError(@NonNull Throwable t) {
        logger.error("处理stream消息发生异常：", t);
    }
}
