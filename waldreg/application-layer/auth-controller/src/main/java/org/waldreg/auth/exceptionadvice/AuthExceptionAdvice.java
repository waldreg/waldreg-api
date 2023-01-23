package org.waldreg.auth.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.token.exception.AuthenticateFailException;
import org.waldreg.token.exception.CannotBeBlankException;
import org.waldreg.token.exception.PasswordMissMatchException;
import org.waldreg.token.exception.TokenExpiredException;
import org.waldreg.token.exception.UserIdMissMatchException;

@RestControllerAdvice
public class AuthExceptionAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler({CannotBeBlankException.class})
    public ResponseEntity<AuthExceptionTemplate> catchCannotBeBlankException(CannotBeBlankException cannotBeBlankException){
        AuthExceptionTemplate authExceptionTemplate = AuthExceptionTemplate.builder()
                .messages("Cannot be blank user_id or user_password")
                .documetUrl(documentUrl)
                .build();
        return new ResponseEntity<>(authExceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserIdMissMatchException.class, PasswordMissMatchException.class})
    public ResponseEntity<AuthExceptionTemplate> catchMissmatchException(RuntimeException runtimeException){
        AuthExceptionTemplate authExceptionTemplate = AuthExceptionTemplate.builder()
                .messages("Invalid authentication information")
                .documetUrl(documentUrl)
                .build();
        return new ResponseEntity<>(authExceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<AuthExceptionTemplate> catchTokenExpiredException(TokenExpiredException tokenExpiredException){
        AuthExceptionTemplate authExceptionTemplate = AuthExceptionTemplate.builder()
                .messages(tokenExpiredException.getMessage())
                .documetUrl(documentUrl)
                .build();
        return new ResponseEntity<>(authExceptionTemplate, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({AuthenticateFailException.class})
    public ResponseEntity<AuthExceptionTemplate> catchAuthenticateFailException(AuthenticateFailException authenticateFailException){
        AuthExceptionTemplate authExceptionTemplate = AuthExceptionTemplate.builder()
                .messages(authenticateFailException.getMessage())
                .documetUrl(documentUrl)
                .build();
        return new ResponseEntity<>(authExceptionTemplate, HttpStatus.UNAUTHORIZED);
    }
}
