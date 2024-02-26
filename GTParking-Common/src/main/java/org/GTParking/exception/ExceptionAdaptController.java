package org.GTParking.exception;

import org.GTParking.bean.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdaptController {

    @ExceptionHandler(value = RuntimeException.class)
    public Result runtimeException(RuntimeException e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public Result exception(Exception e) {
        e.printStackTrace();
        return Result.fail(e.getMessage());
    }
}
