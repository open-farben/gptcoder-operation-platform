package cn.com.farben.commons.web.filter;

import cn.com.farben.commons.web.utils.TrimRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TrimFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //自定义TrimRequestWrapper，在这里实现参数去空
        TrimRequestWrapper requestWrapper = new TrimRequestWrapper(request);
        filterChain.doFilter(requestWrapper, response);
    }
}
