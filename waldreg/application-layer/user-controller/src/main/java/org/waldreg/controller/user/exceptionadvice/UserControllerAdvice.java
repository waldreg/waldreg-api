package org.waldreg.controller.user.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.user.exception.DuplicatedUserIdException;
import org.waldreg.user.exception.InvalidRangeException;
import org.waldreg.user.exception.UnknownIdException;
import org.waldreg.user.exception.UnknownUserIdException;

@RestControllerAdvice
public class UserControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler({DuplicatedUserIdException.class})
    public ResponseEntity<ExceptionTemplate> catchDuplicatedUserIdException(DuplicatedUserIdException duplicatedUserIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message("Duplicated user_id")
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidRangeException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidRangeException(InvalidRangeException invalidRangeException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message("Invalid range")
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownIdException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownIdException(UnknownIdException unknownIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message("Unknown id")
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownUserIdException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownUserIdException(UnknownUserIdException unknownUserIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message("Unknown user id")
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
