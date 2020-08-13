package com.softserve.edu.exception;

import com.softserve.edu.dto.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class WebExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseStatus handlerClientError(Exception e){
        e.printStackTrace();
        return new ResponseStatus(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseStatus handlerPageNotFound(Exception e){
        e.printStackTrace();
        return new ResponseStatus(HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    public ResponseStatus handlerServerError(Exception e){
        e.printStackTrace();
        return new ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
