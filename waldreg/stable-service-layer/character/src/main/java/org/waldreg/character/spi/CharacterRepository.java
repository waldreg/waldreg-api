package org.waldreg.character.spi;

import org.waldreg.character.dto.CharacterDto;

public interface CharacterRepository{

    void createCharacter(CharacterDto characterDto);

    CharacterDto readCharacter(String characterName);

}
