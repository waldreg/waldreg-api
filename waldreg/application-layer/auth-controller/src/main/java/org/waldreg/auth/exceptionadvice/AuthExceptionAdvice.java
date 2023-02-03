package org.waldreg.auth.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.core.template.exception.ExceptionTemplate;
import org.waldreg.token.exception.AuthenticateFailException;
import org.waldreg.token.exception.PasswordMissMatchException;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.exception.UserIdMissMatchException;

@RestControllerAdvice
public class AuthExceptionAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler({UserIdMissMatchException.class, PasswordMissMatchException.class})
    public ResponseEntity<ExceptionTemplate> catchMissmatchException(RuntimeException runtimeException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("AUTH-404")
                .message("Invalid authentication information")
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<ExceptionTemplate> catchTokenExpiredException(TokenExpiredException tokenExpiredException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("AUTH-401")
                .message(tokenExpiredException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthenticateFailException.class})
    public ResponseEntity<ExceptionTemplate> catchAuthenticateFailException(AuthenticateFailException authenticateFailException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("AUTH-402")
                .message(authenticateFailException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.UNAUTHORIZED);
    }
}
