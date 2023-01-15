package org.waldreg.character.spi;

import java.util.List;
import org.waldreg.character.dto.CharacterDto;

public interface CharacterRepository{

    void createCharacter(CharacterDto characterDto);

    CharacterDto readCharacter(String characterName);

    List<CharacterDto> readCharacterList();

    void updateCharacter(String targetName, CharacterDto changedCharacter);

}
