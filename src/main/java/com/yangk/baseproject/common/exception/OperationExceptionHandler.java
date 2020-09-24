package com.yangk.baseproject.common.exception;

import com.yangk.baseproject.common.exception.BusinessRuntimeException;
import com.yangk.baseproject.domain.response.ResponseMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;


@ControllerAdvice
public class OperationExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(OperationExceptionHandler.class);
    
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseMsg defaultErrorHandler(Exception e, HttpServletRequest request, HttpServletResponse response){
        // 打印异常信息：
        if (e != null) {
            String errorMsg = getExceptionMsg(e);
            if (e instanceof BusinessRuntimeException) {
                logger.error("业务性异常", errorMsg);
                return ResponseMsg.buildFailMsg(errorMsg);
            } /*else if ((e instanceof HystrixRuntimeException)
                    || (e instanceof HystrixBadRequestException) 
                    || (e instanceof HystrixTimeoutException)
                    || (e instanceof FeignException)) {
                
                logger.error("Feign远程调用异常", e);
            } */else if(e instanceof SQLException) {
                logger.error("数据库操作异常", e);
            } else if(e instanceof Exception) {
                logger.error("未知系统异常", e);
            }
            return ResponseMsg.buildFailMsg(errorMsg);
        }
        return ResponseMsg.buildFailMsg();
    }

    private String getExceptionMsg(Exception e) {
        if (e == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage() == null ? "" : e.getMessage());
        StackTraceElement[] steArr = e.getStackTrace();
        int index = 0;
        for (StackTraceElement ste : steArr) {
            if (index <= 10 || ste.getClassName().contains("com.yangk")) {
                sb.append("\n").append(ste.getClassName()).append("(").append(ste.getMethodName()).append(".")
                    .append(ste.getLineNumber()).append(")");
            }
        }
        return sb.toString();
    }
}
