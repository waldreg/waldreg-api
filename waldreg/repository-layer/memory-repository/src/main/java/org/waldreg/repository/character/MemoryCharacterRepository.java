package org.waldreg.repository.character;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.exception.DuplicatedCharacterException;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;

@Repository
public class MemoryCharacterRepository implements CharacterRepository{

    private final MemoryCharacterStorage memoryCharacterStorage;
    private final MemoryUserStorage memoryUserStorage;
    private final CharacterMapper characterMapper;

    @Autowired
    public MemoryCharacterRepository(MemoryCharacterStorage memoryCharacterStorage,
            MemoryUserStorage memoryUserStorage,
            CharacterMapper characterMapper){
        this.memoryCharacterStorage = memoryCharacterStorage;
        this.memoryUserStorage = memoryUserStorage;
        this.characterMapper = characterMapper;
    }


    @Override
    public synchronized void createCharacter(CharacterDto characterDto){
        throwIfDuplicatedCharacterNameDetected(characterDto);
        memoryCharacterStorage.createCharacter(
                characterMapper.characterDtoToDomain(characterDto)
        );
    }

    private void throwIfDuplicatedCharacterNameDetected(CharacterDto characterDto){
        if (memoryCharacterStorage.readCharacterByName(characterDto.getCharacterName()) != null){
            throw new DuplicatedCharacterException(characterDto.getCharacterName());
        }
    }

    @Override
    public CharacterDto readCharacter(String characterName){
        throwIfCharacterDoesNotExist(characterName);
        Character character = memoryCharacterStorage.readCharacterByName(characterName);
        return characterMapper.characterDomainToDto(character);
    }

    @Override
    public CharacterDto readCharacterByUserId(int id){
        User user = memoryUserStorage.readUserById(id);
        throwIfCharacterDoesNotExist(user.getCharacter().getCharacterName());
        return characterMapper.characterDomainToDto(user.getCharacter());
    }

    private void throwIfCharacterDoesNotExist(String characterName){
        if (memoryCharacterStorage.readCharacterByName(characterName) == null){
            throw new UnknownCharacterException(characterName);
        }
    }


    @Override
    public List<CharacterDto> readCharacterList(){
        Map<String, Character> characterMap = memoryCharacterStorage.readAllCharacter();
        List<CharacterDto> characterDtoList = new ArrayList<>();
        for (Map.Entry<String, Character> characterEntry : characterMap.entrySet()){
            characterDtoList.add(characterMapper.characterDomainToDto(characterEntry.getValue()));
        }
        return characterDtoList;
    }


    @Override
    public void updateCharacter(String targetName, CharacterDto changedCharacter){

    }

    @Override
    public void deleteCharacter(String characterName){

    }

}
