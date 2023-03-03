package org.waldreg.user.spi;

import org.waldreg.character.dto.CharacterDto;

public interface UsersCharacterRepository{

    boolean isExistCharacterName(String characterName);

}
