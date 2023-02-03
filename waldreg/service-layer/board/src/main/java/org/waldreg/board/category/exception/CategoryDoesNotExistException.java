package org.waldreg.board.category.exception;

public class CategoryDoesNotExistException extends RuntimeException{

    public CategoryDoesNotExistException(int categoryId){
        super("Category does not exist CategoryId : " + categoryId);
    }

}
