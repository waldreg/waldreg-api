package org.waldreg.controller.board.exceptionadvice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.BoardTitleOverFlowException;
import org.waldreg.board.exception.CategoryDoesNotExistException;
import org.waldreg.board.exception.CategoryNameOverFlowException;
import org.waldreg.board.exception.CommentDoesNotExistException;
import org.waldreg.board.exception.ContentOverFlowException;
import org.waldreg.board.exception.DuplicateCategoryNameException;
import org.waldreg.board.exception.FileDoesNotSavedException;
import org.waldreg.board.exception.InvalidRangeException;
import org.waldreg.board.exception.ReactionTypeDoesNotExistException;
import org.waldreg.board.file.exception.UnknownFileId;
import org.waldreg.core.template.exception.ExceptionTemplate;

@RestControllerAdvice
public class BoardControllerAdvice{

    private final String documentUrl = "docs.waldreg.org";

    @ExceptionHandler(BoardDoesNotExistException.class)
    public ResponseEntity<ExceptionTemplate> catchBoardDoesNotExistException(BoardDoesNotExistException boardDoesNotExistException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-401")
                .message(boardDoesNotExistException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryDoesNotExistException.class)
    public ResponseEntity<ExceptionTemplate> catchCategoryDoesNotExistException(CategoryDoesNotExistException categoryDoesNotExistException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-403")
                .message(categoryDoesNotExistException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CommentDoesNotExistException.class)
    public ResponseEntity<ExceptionTemplate> catchCommentDoesNotExistException(CommentDoesNotExistException commentDoesNotExistException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-406")
                .message(commentDoesNotExistException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ContentOverFlowException.class)
    public ResponseEntity<ExceptionTemplate> catchContentOverFlowException(ContentOverFlowException contentOverFlowException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-405")
                .message(contentOverFlowException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateCategoryNameException.class)
    public ResponseEntity<ExceptionTemplate> catchDuplicateCategoryNameException(DuplicateCategoryNameException duplicateCategoryNameException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-412")
                .message(duplicateCategoryNameException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileDoesNotSavedException.class)
    public ResponseEntity<ExceptionTemplate> catchFileDoesNotSavedException(FileDoesNotSavedException fileDoesNotSavedException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-502")
                .message(fileDoesNotSavedException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidRangeException.class)
    public ResponseEntity<ExceptionTemplate> catchInvalidRangeException(InvalidRangeException invalidRangeException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-404")
                .message(invalidRangeException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ReactionTypeDoesNotExistException.class)
    public ResponseEntity<ExceptionTemplate> catchReactionTypeDoesNotExistException(ReactionTypeDoesNotExistException reactionTypeDoesNotExistException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-402")
                .message(reactionTypeDoesNotExistException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownFileId.class})
    public ResponseEntity<ExceptionTemplate> catchUnknownFileIdException(UnknownFileId unknownFileId){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-408")
                .message(unknownFileId.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownFileId.class})
    public ResponseEntity<ExceptionTemplate> catchBoardTitleOverFlowException(BoardTitleOverFlowException boardTitleOverFlowException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-414")
                .message(boardTitleOverFlowException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnknownFileId.class})
    public ResponseEntity<ExceptionTemplate> catchCategoryNameOverFlowException(CategoryNameOverFlowException categoryNameOverFlowException){
        ExceptionTemplate exceptionTemplate = ExceptionTemplate.builder()
                .code("BOARD-416")
                .message(categoryNameOverFlowException.getMessage())
                .documentUrl(documentUrl)
                .build();
        return new ResponseEntity<>(exceptionTemplate, HttpStatus.BAD_REQUEST);
    }

}
