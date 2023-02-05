package org.waldreg.config.globalexceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.core.template.exception.ExceptionTemplate;

@RestControllerAdvice
public class RequestValidationAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionTemplate> catchValidationException(MethodArgumentNotValidException methodArgumentNotValidException){
        String[] messages = methodArgumentNotValidException.getBindingResult().getAllErrors().get(0).getDefaultMessage().split(" ");
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(messages[0])
                .message(getMessage(messages))
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    private String getMessage(String[] message){
        StringBuilder sb = new StringBuilder();
        for(int i = 1; i < message.length; i++){
            sb.append(message[i]);
            if(i < message.length-1) sb.append(" ");
        }
        return sb.toString();
    }

}
