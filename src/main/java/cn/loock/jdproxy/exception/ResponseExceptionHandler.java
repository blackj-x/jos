package cn.loock.jdproxy.exception;

import cn.loock.jdproxy.bean.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;


/**
 * Created with IntelliJ IDEA.
 * User: liudi
 * Date: 17/1/3
 * Time: 下午3:04
 */
@EnableWebMvc
@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(ResponseExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleControllerException(HttpServletRequest req, Exception ex) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setResult(ex.getClass());
        if (ex instanceof IllegalRequestException) {
            String errorMessage = ex.getMessage();
            int code = ErrorMessageUtil.getErrorCode(errorMessage);
            responseResult.setCode(code);
            responseResult.setMessage(ErrorMessageUtil.getErrorMessage(errorMessage));
            logger.warn(String.valueOf(req.getRequestURL()), ex);
            return new ResponseEntity<Object>(responseResult, ErrorMessageUtil.getHttpStatus(errorMessage));
        } else {
            responseResult.setCode(500);
            responseResult.setMessage(ErrorMessageUtil.getErrorMessage(ErrorMessageUtil.SYSTEM_ERROR));
            logger.error(String.valueOf(req.getRequestURL()), ex);
            return new ResponseEntity<Object>(responseResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}