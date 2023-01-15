package org.waldreg.character.exception;

public class DuplicatedCharacterException extends RuntimeException{

    public DuplicatedCharacterException(String characterName){
        super("Duplicated character name detected \"" + characterName + "\"");
    }

}
