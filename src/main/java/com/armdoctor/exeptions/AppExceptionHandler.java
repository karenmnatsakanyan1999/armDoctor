package com.armdoctor.exeptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
@ControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ErrorResponce> validationExceptionHandler(UserValidationException exception){
        ErrorResponce errorResponce = new ErrorResponce(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponce);
    }
    @ExceptionHandler(UserNotFoundExeption.class)
    public ResponseEntity<ErrorResponce> notFoudExceptionhandler(UserNotFoundExeption exception){
        ErrorResponce errorResponce = new ErrorResponce(exception.getMessage(), HttpStatus.NOT_FOUND.value());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponce);
    }
    @ExceptionHandler(ResourceAlreadyExistException.class)
    public ResponseEntity<ErrorResponce> resourceAlreadyExistHandler(ResourceAlreadyExistException exception){
        ErrorResponce errorResponce = new ErrorResponce(exception.getMessage(), HttpStatus.CONFLICT.value());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponce);
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponce> ApiExceptionHandler(ApiException exception){
        ErrorResponce errorResponce = new ErrorResponce(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponce);
    }

}
