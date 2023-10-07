package com.unisinsight.business.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unisinsight.business.common.utils.Results;
import com.unisinsight.framework.common.exception.BaseErrorCode;
import com.unisinsight.framework.common.exception.BaseException;
import com.unisinsight.framework.common.exception.RestException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.util.List;

/**
 * 统一异常处理
 *
 * @author WangYi [wang.yi@unisinsight.com]
 * @date 2020/8/19
 * @since 1.0
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 错误码前缀
     */
    @Value("${unisinsight.error-code-prefix}")
    private String errorCodePrefix;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(MethodArgumentNotValidException e) {
        log.error("", e);
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < errors.size(); i++) {
            if (i > 0) {
                sb.append(";");
            }

            FieldError error = errors.get(i);
            sb.append(error.getField()).append(Strings.isNotBlank(error.getDefaultMessage()) ?
                    error.getDefaultMessage() : "参数错误");
        }

        Results result = Results.error(BaseErrorCode.INVALID_PARAM_ERROR.getErrorCode(), sb.toString());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(Exception e) {
        log.error("", e);
        Results<?> result = Results.error(BaseErrorCode.INVALID_PARAM_ERROR.getErrorCode(),
                e.getMessage().substring(e.getMessage().lastIndexOf(".") + 1));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 请求方法不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleException(HttpRequestMethodNotSupportedException e) {
        log.error("", e);
        Results result = Results.error(BaseErrorCode.HTTP_REQUEST_METHOD_NOT_SUPPORTED_ERROR);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 接口不存在
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleException(NoHandlerFoundException e) {
        log.error("", e);
        Results result = Results.error(BaseErrorCode.API_NOT_EXIST_ERROR);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 参数类型不匹配
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleException(HttpMessageNotReadableException e) {
        log.error("", e);
        Results result = Results.error(BaseErrorCode.PARAM_SWITCH_ERROR);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 内部服务调用异常
     */
    @ExceptionHandler({RestException.class})
    public ResponseEntity<Object> handleException(RestException e) {
        log.error("", e);
        Results result;
        try {
            result = objectMapper.readValue(e.getMessage(), Results.class);
        } catch (Exception ex) {
            result = Results.error(BaseErrorCode.THIRD_PARTY_INTERFACE_FAILED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 内部服务调用异常
     */
    @ExceptionHandler({FeignException.class})
    public ResponseEntity<Object> handleException(FeignException e) {
        log.error("", e);
        Results result;
        try {
            result = objectMapper.readValue(e.contentUTF8(), Results.class);
        } catch (Exception ex) {
            result = Results.error(BaseErrorCode.THIRD_PARTY_INTERFACE_FAILED);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(IllegalArgumentException e) {
        log.error("", e);
        Results result = Results.error(BaseErrorCode.INVALID_PARAM_ERROR.getErrorCode(), e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleException(BaseException e) {
        log.error("", e);
        String errorCode = e.getErrorCode();
        if (errorCode == null) {
            errorCode = BaseErrorCode.SYS_INTERNAL_ERROR.getErrorCode();
        } else {
            errorCode = errorCode.length() == 10 ? errorCode : errorCodePrefix + errorCode;
        }
        Results result = Results.error(errorCode, e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        log.error("", e);
        Results result = Results.error(BaseErrorCode.SYS_INTERNAL_ERROR);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
