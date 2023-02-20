package org.waldreg.character.spi;

import java.util.List;
import java.util.Optional;
import org.waldreg.character.dto.CharacterDto;

public interface CharacterRepository{

    void createCharacter(CharacterDto characterDto);

    Optional<CharacterDto> readCharacter(String characterName);

    CharacterDto readCharacterByUserId(int id);

    List<CharacterDto> readCharacterList();

    void updateCharacter(String targetName, CharacterDto changedCharacter);

    void deleteCharacter(String characterName);

}
