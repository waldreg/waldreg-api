package org.waldreg.repository.character;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.exception.DuplicatedCharacterException;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.repository.MemoryCharacterStorage;

@Repository
public class MemoryCharacterRepository implements CharacterRepository{

    private final MemoryCharacterStorage memoryCharacterStorage;
    private final CharacterMapper characterMapper;

    @Autowired
    public MemoryCharacterRepository(MemoryCharacterStorage memoryCharacterStorage,
            CharacterMapper characterMapper){
        this.memoryCharacterStorage = memoryCharacterStorage;
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
        if(memoryCharacterStorage.readCharacterByName(characterDto.getCharacterName()) != null){
            throw new DuplicatedCharacterException(characterDto.getCharacterName());
        }
    }

    @Override
    public CharacterDto readCharacter(String characterName){
        return null;
    }

    @Override
    public CharacterDto readCharacterByUserId(int id){
        return null;
    }

    @Override
    public List<CharacterDto> readCharacterList(){
        return null;
    }

    @Override
    public void updateCharacter(String targetName, CharacterDto changedCharacter){

    }

    @Override
    public void deleteCharacter(String characterName){

    }

}
