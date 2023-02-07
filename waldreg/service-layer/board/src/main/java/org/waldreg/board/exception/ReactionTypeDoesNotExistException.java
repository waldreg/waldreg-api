package org.waldreg.board.exception;

public class ReactionTypeDoesNotExistException extends RuntimeException{

    public ReactionTypeDoesNotExistException(String reactionType){
        super("ReactionType Does Not Exist\nReactionType:  " + reactionType);
    }

}
