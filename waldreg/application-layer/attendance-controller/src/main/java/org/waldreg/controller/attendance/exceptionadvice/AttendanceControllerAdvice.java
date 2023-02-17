package org.waldreg.controller.attendance.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.InvalidWaiverDateException;
import org.waldreg.attendance.exception.TooFarDateException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.core.template.exception.ExceptionTemplate;

@RestControllerAdvice
public class AttendanceControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(UnknownUsersIdException.class)
    public ResponseEntity<ExceptionTemplate> handleUnknownUsersIdException(UnknownUsersIdException unknownUsersIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-413")
                .message(unknownUsersIdException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DoesNotRegisteredAttendanceException.class)
    public ResponseEntity<ExceptionTemplate> handleDoesNotRegisteredAttendanceException(DoesNotRegisteredAttendanceException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-410")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWaiverDateException.class)
    public ResponseEntity<ExceptionTemplate> handleInvalidWaiverDateException(InvalidWaiverDateException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-416")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TooFarDateException.class)
    public ResponseEntity<ExceptionTemplate> handleTooFarDateException(TooFarDateException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-415")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
