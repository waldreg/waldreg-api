package org.waldreg.controller.character.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.character.exception.DuplicatedCharacterException;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.exception.UnknownPermissionStatusException;

@RestControllerAdvice
public class CharacterControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler({NoPermissionException.class})
    public ResponseEntity<ExceptionTemplate> catchNoPermissionException(NoPermissionException noPermissionException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message("No permission")
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({UnknownPermissionException.class, UnknownPermissionStatusException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownPermissionExceptions(RuntimeException unknownPermissionException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(unknownPermissionException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicatedCharacterException.class})
    public ResponseEntity<ExceptionTemplate> catchDuplicatedCharacterException(DuplicatedCharacterException duplicatedCharacterException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(duplicatedCharacterException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownCharacterException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownCharacterException(UnknownCharacterException unknownCharacterException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .message(unknownCharacterException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
