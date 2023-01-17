package org.waldreg.config.globalexceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RequestValidationAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionTemplate> catchValidationException(MethodArgumentNotValidException methodArgumentNotValidException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(methodArgumentNotValidException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
