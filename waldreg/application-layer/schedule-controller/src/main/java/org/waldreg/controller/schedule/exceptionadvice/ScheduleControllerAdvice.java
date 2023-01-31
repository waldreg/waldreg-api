package org.waldreg.controller.schedule.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.schedule.exception.ContentOverflowException;

@RestControllerAdvice
public class ScheduleControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler({ContentOverflowException.class})
    public ResponseEntity<ExceptionTemplate> catchContentOverflowException(ContentOverflowException contentOverflowException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(contentOverflowException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
