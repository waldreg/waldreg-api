package org.waldreg.character.management;

import java.util.List;
import org.waldreg.character.aop.CharacterInUserReadable;
import org.waldreg.character.dto.CharacterDto;

public interface CharacterManager extends CharacterInUserReadable{

    void createCharacter(CharacterDto characterDto);

    CharacterDto readCharacter(String characterName);

    List<CharacterDto> readCharacterList();

    void updateCharacter(String targetName, CharacterDto changedCharacter);

    void deleteCharacter(String characterName);

}
