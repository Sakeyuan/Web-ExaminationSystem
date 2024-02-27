package com.sake.examination_system.exception;

import com.sake.examination_system.util.MyResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class MyExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public MyResponseEntity<Object> handle(ServiceException s){
        MyResponseEntity<Object> myResponseEntity = new MyResponseEntity<Object>();
        myResponseEntity.error(s.getCode(),s.getMessage());
        return myResponseEntity;
    }
}
