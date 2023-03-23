package org.waldreg.character.aop;

import org.waldreg.character.dto.CharacterDto;

public interface CharacterInUserReadable{

    CharacterDto readCharacterByUserId(int id);

}
