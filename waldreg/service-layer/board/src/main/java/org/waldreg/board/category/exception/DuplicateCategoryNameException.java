package org.waldreg.board.category.exception;

public class DuplicateCategoryNameException extends RuntimeException{

    public DuplicateCategoryNameException(String categoryName){
        super("Duplicated category name : " +categoryName );
    }

}
