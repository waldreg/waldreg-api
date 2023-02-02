package org.waldreg.controller.schedule.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.core.template.exception.ExceptionTemplate;
import org.waldreg.schedule.exception.ContentOverflowException;
import org.waldreg.schedule.exception.InvalidDateFormatException;
import org.waldreg.schedule.exception.InvalidRepeatException;
import org.waldreg.schedule.exception.InvalidSchedulePeriodException;
import org.waldreg.schedule.exception.UnknownScheduleException;

@RestControllerAdvice
public class ScheduleControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler({ContentOverflowException.class})
    public ResponseEntity<ExceptionTemplate> catchContentOverflowException(ContentOverflowException contentOverflowException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(contentOverflowException.getCode())
                .message(contentOverflowException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidDateFormatException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidDateFormatException(InvalidDateFormatException invalidDateFormatException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(invalidDateFormatException.getCode())
                .message(invalidDateFormatException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidRepeatException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidRepeatException(InvalidRepeatException invalidRepeatException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(invalidRepeatException.getCode())
                .message(invalidRepeatException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidSchedulePeriodException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidSchedulePeriodException(InvalidSchedulePeriodException invalidSchedulePeriodException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(invalidSchedulePeriodException.getCode())
                .message(invalidSchedulePeriodException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownScheduleException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownScheduleException(UnknownScheduleException unknownScheduleException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(unknownScheduleException.getCode())
                .message(unknownScheduleException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
