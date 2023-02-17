package org.waldreg.controller.attendance.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.core.template.exception.ExceptionTemplate;

@RestControllerAdvice
public class AttendanceControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(UnknownUsersIdException.class)
    public ResponseEntity<ExceptionTemplate> UnknownUsersIdException(UnknownUsersIdException unknownUsersIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-413")
                .message(unknownUsersIdException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
