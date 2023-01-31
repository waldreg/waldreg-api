package org.waldreg.controller.reward.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.reward.exception.UnknownRewardException;
import org.waldreg.reward.exception.UnknownRewardTagException;
import org.waldreg.reward.exception.UnknownRewardTargetException;

@RestControllerAdvice
public class RewardControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(UnknownRewardException.class)
    public ResponseEntity<ExceptionTemplate> catchUnknownRewardException(UnknownRewardException unknownRewardException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(unknownRewardException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownRewardTagException.class)
    public ResponseEntity<ExceptionTemplate> catchUnknownRewardTagException(UnknownRewardTagException unknownRewardTagException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(unknownRewardTagException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnknownRewardTargetException.class)
    public ResponseEntity<ExceptionTemplate> catchUnknownRewardTargetException(UnknownRewardTargetException unknownRewardTargetException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(unknownRewardTargetException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
