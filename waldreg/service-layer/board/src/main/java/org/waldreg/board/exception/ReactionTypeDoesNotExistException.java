package org.waldreg.board.exception;

public class ReactionTypeDoesNotExistException extends RuntimeException{

    public ReactionTypeDoesNotExistException(String reactionType){
        super("Invalid reaction type reactionType: " + reactionType);
    }

}
