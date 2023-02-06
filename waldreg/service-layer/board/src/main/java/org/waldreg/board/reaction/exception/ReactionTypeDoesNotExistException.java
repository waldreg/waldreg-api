package org.waldreg.board.reaction.exception;

public class ReactionTypeDoesNotExistException extends RuntimeException{

    public ReactionTypeDoesNotExistException(String reactionType){
        super("ReactionType Does Not Exist\nReactionType:  " + reactionType);
    }

}
