package cn.com.farben.commons.web.handler;

import cn.com.farben.commons.errorcode.enums.ErrorCodeEnum;
import cn.com.farben.commons.errorcode.exception.BusinessException;
import cn.com.farben.commons.errorcode.exception.EmptyRequestBodyException;
import cn.com.farben.commons.errorcode.exception.ModelIOException;
import cn.com.farben.commons.web.utils.StreamUtils;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.com.farben.commons.web.ResultData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.ValidationException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * 创建时间: 2021/10/30
 * @author ltg
 * @apiNote 系统异常统一处理
 * <p>统一处理{@link Exception}系统异常、{@link BusinessException}业务处理异常
 * {@link ConstraintViolationException}普通传参异常、{@link BindException}实体对象传参异常、
 * {@link ValidationException}请求参数校验异常</p>
 *
 */
@Order(value = 2)
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Log logger = LogFactory.get();

    /**
     * 错误处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultData<Void> handleException(Exception e) {
        logger.error("系统内部异常", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.SYSTEM_ERROR_B0001).build();
    }

    /**
     * 业务异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = BusinessException.class)
    public <E extends BusinessException> ResultData<Void> handleBusinessException(E e) {
        logger.error("业务出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        String message = e.getMessage();
        if (CharSequenceUtil.isBlank(message)) {
            return new ResultData.Builder<Void>().error(e.getErrorCodeEnum()).build();
        } else {
            return new ResultData.Builder<Void>().error(e.getErrorCodeEnum()).message(message).build();
        }
    }

    /**
     * 统一处理请求参数校验（普通传参）
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleConstraintViolationException(ConstraintViolationException e) {
        logger.error("统一处理请求参数校验（普通传参）", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        StringJoiner message = new StringJoiner(",");
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            Path path = violation.getPropertyPath();
            List<String> pathArr = CharSequenceUtil.split(path.toString(), ".");
            message.add(pathArr.get(1) + violation.getMessage());
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0400).message(message.toString()).build();
    }

    /**
     * 统一处理请求参数校验（实体对象传参）
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleConstraintViolationException(BindException e) {
        logger.error("统一处理请求参数校验（实体对象传参）", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        StringJoiner message = new StringJoiner(",");
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.add(error.getField() + error.getDefaultMessage());
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0400).message(message.toString()).build();
    }

    /**
     * 统一处理请求参数校验
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleValidationException(ValidationException e) {
        logger.error("统一处理请求参数校验", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        StringJoiner message = new StringJoiner(",");
        if(e instanceof ConstraintViolationException exs){
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                //打印验证不通过的信息
                message.add(item.getMessage());
            }
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0400).message(message.length() == 0 ?
                e.getMessage() : message.toString()).build();
    }

    /**
     * request body未传参异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleRequestBodyMissingException(HttpMessageNotReadableException e) {
        logger.error("request body参数有误", e);
        String message = ErrorCodeEnum.USER_ERROR_A0410.getDescribe();
        if (e.getMessage().contains("JSON parse error")) {
            message = "参数转换出错，请确认所有参数无误";
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0410).message(message).build();
    }

    /**
     * request body是空的异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = EmptyRequestBodyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleEmptyRequestBodyException(EmptyRequestBodyException e) {
        logger.error("空的request body", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        String message = e.getMessage();
        if (CharSequenceUtil.isBlank(message)) {
            return new ResultData.Builder<Void>().error(e.getErrorCodeEnum()).build();
        } else {
            return new ResultData.Builder<Void>().error(e.getErrorCodeEnum()).message(message).build();
        }
    }

    /**
     * request parameter未传参异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleRequestParameterMissingException(MissingServletRequestParameterException e) {
        logger.error("request parameter未传参", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        String parameterName = e.getParameterName();
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0410).message(String.format("未传入%s", parameterName)).build();
    }

    /**
     * request part未传参异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = MissingServletRequestPartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleRequestPartMissingException(MissingServletRequestPartException e) {
        logger.error("必要文件未传入", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0410).message(String.format("未传入%s", e.getRequestPartName())).build();
    }

    /**
     * 方法参数类型丢失异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("转换参数出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0410).message(String.format("参数%s不正确", e.getName())).build();
    }

    /**
     * 不支持的方法异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error("不支持的方法", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0410).message(String.format("不支持的方法%s", e.getMethod())).build();
    }

    /**
     * 不传文件异常处理
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = MultipartException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleMultipartException(MultipartException e) {
        logger.error("上传文件异常", e);
        if (e.getCause().getMessage().contains("Connection terminated as request was larger than")) {
            return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0420).message("上传文件过大").build();
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0410).message("没有传入必要文件").build();
    }

    /**
     * 访问模型服务出错
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = ModelIOException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ResultData<Void> handleModelIOExceptionException(ModelIOException e) {
        logger.error("访问模型服务出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.SERVICE_ERROR_C0504).build();
    }

    /**
     * 不支持的媒体类型
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultData<Void> handleModelIOExceptionException(HttpMediaTypeNotSupportedException e) {
        logger.error("不支持的Content-Type", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A1007)
                .message(CharSequenceUtil.format("不支持的Content-Type: [{}]", e.getContentType())).build();
    }

    /**
     * 处理第三方服务请求出错
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = WebClientRequestException.class)
    public ResultData<Void> handleClientException(WebClientRequestException e) {
        logger.error("调用第三方服务出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, null);
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.SERVICE_ERROR_C0001).build();
    }

    /**
     * 处理第三方服务响应出错
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = WebClientResponseException.class)
    public ResultData<Void> handleWebClientResponseException(WebClientResponseException e) {
        logger.error("调用第三方服务出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, e.getStatusCode());
        }
        if (e.getMessage().startsWith("422 Unprocessable Entity")) {
            return new ResultData.Builder<Void>().error(ErrorCodeEnum.SERVICE_ERROR_C0001).message("参数不正确").build();
        }
        if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
            return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0320).message("访问未授权或授权已过期").build();
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.SERVICE_ERROR_C0001).build();
    }

    /**
     * 处理参数错误
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultData<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数出错", e);
        StringJoiner message = new StringJoiner(";");
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        for (ObjectError error : allErrors) {
            message.add(error.getDefaultMessage());
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0402).message(message.toString()).build();
    }

    /**
     * 处理访问其它服务错误
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = HttpClientErrorException.class)
    public ResultData<Void> handleHttpClientErrorException(HttpClientErrorException e) {
        logger.error("访问其它服务出错", e);
        if (StreamUtils.isStreamRequest()) {
            return StreamUtils.handleStreamResponse(e, e.getStatusCode());
        }
        if (e instanceof HttpClientErrorException.Forbidden) {
            return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0320).message("访问未授权或授权已过期").build();
        }
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.SERVICE_ERROR_C0001).build();
    }

    /**
     * 处理资源不存在错误
     * @param e 异常
     * @return 异常消息
     */
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResultData<Void> handleHttpClientErrorException(NoResourceFoundException e) {
        logger.error("资源不存在", e);
        return new ResultData.Builder<Void>().error(ErrorCodeEnum.USER_ERROR_A0600).message("访问资源不存在").build();
    }
}
