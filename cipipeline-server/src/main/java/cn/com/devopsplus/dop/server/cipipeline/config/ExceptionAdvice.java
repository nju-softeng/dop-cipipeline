package cn.com.devopsplus.dop.server.cipipeline.config;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(RuleException.class)
    @ResponseBody
    public static Result doException(Exception e){
        Result result = new Result();
        result.setStatus("500");
        result.setMessage(e.getMessage());
        return result;
    }
}
