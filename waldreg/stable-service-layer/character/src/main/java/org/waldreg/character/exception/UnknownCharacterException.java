package org.waldreg.character.exception;

public class UnknownCharacterException extends RuntimeException{

    public UnknownCharacterException(String characterName){
        super("Can not find character named \"" + characterName + "\"");
    }

}
