package org.waldreg.character.management;

import java.util.List;
import org.waldreg.character.dto.CharacterDto;

public interface CharacterManager{

    void createCharacter(CharacterDto characterDto);

    CharacterDto readCharacter(String characterName);

    List<CharacterDto> readCharacterList();

}
