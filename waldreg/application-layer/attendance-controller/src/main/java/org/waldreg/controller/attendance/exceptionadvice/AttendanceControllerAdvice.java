package org.waldreg.controller.attendance.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.attendance.exception.AlreadyAttendanceException;
import org.waldreg.attendance.exception.DoesNotRegisteredAttendanceException;
import org.waldreg.attendance.exception.DoesNotStartedAttendanceException;
import org.waldreg.attendance.exception.InvalidDateException;
import org.waldreg.attendance.exception.InvalidWaiverDateException;
import org.waldreg.attendance.exception.TooEarlyDateException;
import org.waldreg.attendance.exception.TooFarDateException;
import org.waldreg.attendance.exception.UnknownAttendanceTypeException;
import org.waldreg.attendance.exception.UnknownRewardTagIdException;
import org.waldreg.attendance.exception.UnknownUsersIdException;
import org.waldreg.attendance.exception.UnknownWaiverIdException;
import org.waldreg.attendance.exception.WrongAttendanceIdentifyException;
import org.waldreg.core.template.exception.ExceptionTemplate;

@RestControllerAdvice
public class AttendanceControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(DoesNotRegisteredAttendanceException.class)
    public ResponseEntity<ExceptionTemplate> handleDoesNotRegisteredAttendanceException(DoesNotRegisteredAttendanceException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-410")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownWaiverIdException.class)
    public ResponseEntity<ExceptionTemplate> handleUnknownWaiverIdException(UnknownWaiverIdException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-411")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownAttendanceTypeException.class)
    public ResponseEntity<ExceptionTemplate> handleUnknownAttendanceTypeException(UnknownAttendanceTypeException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-412")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownUsersIdException.class)
    public ResponseEntity<ExceptionTemplate> handleUnknownUsersIdException(UnknownUsersIdException unknownUsersIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-413")
                .message(unknownUsersIdException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<ExceptionTemplate> handleInvalidDateException(InvalidDateException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-414")
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

    @ExceptionHandler(TooEarlyDateException.class)
    public ResponseEntity<ExceptionTemplate> handleTooEarlyDateException(TooEarlyDateException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-416")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidWaiverDateException.class)
    public ResponseEntity<ExceptionTemplate> handleInvalidWaiverDateException(InvalidWaiverDateException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-417")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AlreadyAttendanceException.class)
    public ResponseEntity<ExceptionTemplate> handleAlreadyAttendanceException(AlreadyAttendanceException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-418")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(WrongAttendanceIdentifyException.class)
    public ResponseEntity<ExceptionTemplate> handleWrongAttendanceIdentifyException(WrongAttendanceIdentifyException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-419")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownRewardTagIdException.class)
    public ResponseEntity<ExceptionTemplate> handleUnknownRewardTagIdException(UnknownRewardTagIdException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-420")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DoesNotStartedAttendanceException.class)
    public ResponseEntity<ExceptionTemplate> handleDoesNotStartedAttendanceException(DoesNotStartedAttendanceException exception){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("ATTENDANCE-421")
                .message(exception.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
