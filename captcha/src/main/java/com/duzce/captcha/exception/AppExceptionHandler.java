package com.duzce.captcha.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(value = {NoHandlerFoundException.class, ResourceNotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handlerNotFound(HttpServletRequest request, Exception e) {
        System.err.println(request.getRequestURL() + " istegine karsilik karsilayici bulunamadi. Hata mesaji: "
                + e.getMessage());
        e.printStackTrace();
        return "404";
    }


    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalServerError(HttpServletRequest request, Exception e) {
        System.err.println(request.getRequestURL() + " istegi gerceklestirilirken bir hata olustu. Hata mesaji: "
                + e.getMessage());
        e.printStackTrace();
        return "error";
    }

}
