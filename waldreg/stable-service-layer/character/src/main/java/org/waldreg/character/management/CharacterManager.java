package org.waldreg.character.management;

import org.waldreg.character.dto.CharacterDto;

public interface CharacterManager{

    void createCharacter(CharacterDto characterDto);

    CharacterDto readCharacter(String characterName);

}
