package cn.com.farben.commons.web.interceptor;

import cn.com.farben.commons.web.constants.AuthenticationConstants;
import cn.com.farben.commons.web.utils.TokenUtils;
import cn.com.farben.commons.web.utils.UserThreadLocal;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息拦截器
 */
@Component
public class UserInfoInterceptor implements HandlerInterceptor {
    private static final Log logger = LogFactory.get();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(AuthenticationConstants.TOKEN_REQUEST_HEADER);
        if (CharSequenceUtil.isNotBlank(authorizationHeader)) {
            String token = authorizationHeader.replace(AuthenticationConstants.TOKEN_HEAD, "").trim();
            logger.info("用户令牌:[{}]", token);
            UserThreadLocal.put(TokenUtils.getUserInfoByToken(token));
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 删除缓存的用户信息
        UserThreadLocal.remove();
    }
}
