package cn.com.farben.commons.web.interceptor;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 日志拦截器
 */
@Component
public class LogInterceptor implements HandlerInterceptor {
    private static final Log logger = LogFactory.get();

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws Exception {
        logger.info("用户访问[{}]", request.getRequestURI());
        if(handler instanceof HandlerMethod methodHandler) {
            logger.info("用户准备调用[{}]", methodHandler.getShortLogMessage());
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                           ModelAndView modelAndView) {
        logger.info("用户访问[{}]完成", request.getRequestURI());
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler,
                                Exception ex) {
        logger.info("用户访问[{}]返回", request.getRequestURI());
    }
}
