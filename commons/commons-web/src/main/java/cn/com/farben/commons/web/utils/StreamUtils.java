package cn.com.farben.commons.web.utils;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.EmptyRequestBodyException;
import cn.com.farben.commons.errorcode.exception.ModelIOException;
import cn.com.farben.commons.web.ResultData;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Objects;

public class StreamUtils {
    private static final Log logger = LogFactory.get();

    /**
     * 判断是不是只接受stream的请求
     * @return true或者false
     */
    public static boolean isStreamRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String accept = request.getHeader("accept");
        return CharSequenceUtil.isNotBlank(accept) && accept.contains("text/event-stream");
    }

    /**
     * 处理出错情况下返回stream的响应
     * @param e 错误信息
     * @param statusCode 状态码
     * @return null
     */
    public static ResultData<Void> handleStreamResponse(Exception e, HttpStatusCode statusCode) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        String message = null;
        ErrorCodeEnum errorCodeEnum = getErrorCodeEnum(e);
        if (statusCode == HttpStatus.FORBIDDEN) {
            message = "code:403, 访问未授权或授权已过期";
        }

        if (Objects.nonNull(response)) {
            if (CharSequenceUtil.isBlank(message)) {
                message = "code:" + errorCodeEnum.getCode();
            }
            response.setCharacterEncoding( "UTF-8" );
            response.setContentType("text/event-stream");
            PrintWriter pw;
            try {
                pw = response.getWriter();
                pw.write("data:|" + message + "|\n\n<|endoftext|>");
                pw.flush();
            } catch (IOException ex) {
                logger.error("获取response的writer出错", ex);
            }
            return null;
        } else {
            if (CharSequenceUtil.isBlank(message)) {
                message = e.getMessage();
            }
            if (CharSequenceUtil.isBlank(message)) {
                return new ResultData.Builder<Void>().error(errorCodeEnum).build();
            } else {
                return new ResultData.Builder<Void>().error(errorCodeEnum).message(message).build();
            }
        }
    }

    private static ErrorCodeEnum getErrorCodeEnum(Exception e) {
        ErrorCodeEnum errorCodeEnum;
        if (e instanceof BusinessException be) {
            errorCodeEnum = be.getErrorCodeEnum();
        } else if (e instanceof SQLException) {
            errorCodeEnum = ErrorCodeEnum.SYSTEM_ERROR_B0500;
        } else if (e instanceof EmptyRequestBodyException erbe) {
            errorCodeEnum = erbe.getErrorCodeEnum();
        } else if (e instanceof BindException || e instanceof ValidationException) {
            errorCodeEnum = ErrorCodeEnum.USER_ERROR_A0400;
        } else if (e instanceof HttpMessageNotReadableException || e instanceof MissingServletRequestParameterException
        || e instanceof MissingServletRequestPartException || e instanceof MethodArgumentTypeMismatchException ||
        e instanceof HttpRequestMethodNotSupportedException || e instanceof MultipartException) {
            errorCodeEnum = ErrorCodeEnum.USER_ERROR_A0410;
        } else if (e instanceof ModelIOException) {
            errorCodeEnum = ErrorCodeEnum.SERVICE_ERROR_C0504;
        } else if (e instanceof HttpMediaTypeNotSupportedException) {
            errorCodeEnum = ErrorCodeEnum.USER_ERROR_A1007;
        } else {
            errorCodeEnum = ErrorCodeEnum.SYSTEM_ERROR_B0001;
        }
        return errorCodeEnum;
    }

    private StreamUtils() {
        throw new IllegalStateException("工具类不允许实例化");
    }
}
