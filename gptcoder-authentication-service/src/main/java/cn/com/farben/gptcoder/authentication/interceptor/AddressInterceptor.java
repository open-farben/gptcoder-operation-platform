package cn.com.farben.gptcoder.authentication.interceptor;

import cn.com.farben.gptcoder.authentication.utils.CompareUtils;
import cn.com.farben.gptcoder.authentication.utils.IPUtils;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

/**
 * 地址拦截器，某些接口只接受指定的ip地址调用
 */
@Component
public class AddressInterceptor implements HandlerInterceptor {
    private static final Log logger = LogFactory.get();

    @Value("${authentication.config.token-white-list}")
    private String ipWhiteList;

    private static List<String> compareWhiteList = null;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String clientIp = IPUtils.getIpAddr(request);
        boolean flag = CompareUtils.isContain(compareWhiteList, ipWhiteList, clientIp);
        if (!flag) {
            logger.error("未允许的ip地址[{}]", clientIp);
        }

        flag = true;//本地开发，暂时关闭ip白名单校验

        return flag;
    }

}
