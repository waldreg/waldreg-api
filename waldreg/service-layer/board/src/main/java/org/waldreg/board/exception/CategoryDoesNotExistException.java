package org.waldreg.board.exception;

public class CategoryDoesNotExistException extends RuntimeException{

    public CategoryDoesNotExistException(int categoryId){
        super("Category Does Not Exist\nCategoryId:  " + categoryId);
    }

}
