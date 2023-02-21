package org.waldreg.controller.teambuilding.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.core.template.exception.ExceptionTemplate;
import org.waldreg.teambuilding.exception.ContentOverflowException;
import org.waldreg.teambuilding.exception.DuplicateUserSelectException;
import org.waldreg.teambuilding.exception.DuplicatedTeamNameException;
import org.waldreg.teambuilding.exception.InvalidRangeException;
import org.waldreg.teambuilding.exception.InvalidTeamCountException;
import org.waldreg.teambuilding.exception.InvalidUserWeightException;
import org.waldreg.teambuilding.exception.UnknownTeamBuildingIdException;
import org.waldreg.teambuilding.exception.UnknownTeamIdException;
import org.waldreg.teambuilding.exception.UnknownUserIdException;

@RestControllerAdvice
public class TeamBuildingControllerAdvice{

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

    @ExceptionHandler({DuplicatedTeamNameException.class})
    public ResponseEntity<ExceptionTemplate> catchDuplicatedTeamNameException(DuplicatedTeamNameException duplicatedTeamNameException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-406")
                .message(duplicatedTeamNameException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidRangeException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidRangeException(InvalidRangeException invalidRangeException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-409")
                .message(invalidRangeException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({DuplicateUserSelectException.class})
    public ResponseEntity<ExceptionTemplate> catchDuplicateUserSelectException(DuplicateUserSelectException duplicateUserSelectException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-405")
                .message(duplicateUserSelectException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidTeamCountException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidTeamCountException(InvalidTeamCountException invalidTeamCountException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code(invalidTeamCountException.getCode())
                .message(invalidTeamCountException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidUserWeightException.class})
    public ResponseEntity<ExceptionTemplate> catchInvalidUserWeightException(InvalidUserWeightException invalidUserWeightException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-404")
                .message(invalidUserWeightException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownTeamBuildingIdException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownTeamBuildingIdException(UnknownTeamBuildingIdException unknownTeamBuildingIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-400")
                .message(unknownTeamBuildingIdException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownTeamIdException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownTeamIdException(UnknownTeamIdException unknownTeamIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-407")
                .message(unknownTeamIdException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownUserIdException.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownUserIdException(UnknownUserIdException unknownUserIdException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("TEAMBUILDING-412")
                .message(unknownUserIdException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
