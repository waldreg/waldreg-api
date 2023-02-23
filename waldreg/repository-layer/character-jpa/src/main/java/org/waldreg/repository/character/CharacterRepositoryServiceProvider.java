package org.waldreg.repository.character;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.domain.character.Character;
import org.waldreg.repository.character.mapper.CharacterRepositoryMapper;
import org.waldreg.repository.character.repository.JpaCharacterRepository;
import org.waldreg.repository.character.repository.JpaPermissionRepository;

@Repository
public class CharacterRepositoryServiceProvider implements CharacterRepository{

    private final JpaCharacterRepository characterRepository;
    private final JpaPermissionRepository permissionRepository;
    private final CharacterRepositoryMapper characterRepositoryMapper;

    @Autowired
    CharacterRepositoryServiceProvider(JpaCharacterRepository characterRepository,
                                        JpaPermissionRepository permissionRepository,
                                        CharacterRepositoryMapper characterRepositoryMapper){
        this.characterRepository = characterRepository;
        this.permissionRepository = permissionRepository;
        this.characterRepositoryMapper = characterRepositoryMapper;
    }

    @Override
    @Transactional
    public void createCharacter(CharacterDto characterDto){
        Character character = characterRepositoryMapper.characterDtoToCharacter(characterDto);
        characterRepository.save(character);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CharacterDto> readCharacter(String characterName){
        Optional<Character> character = characterRepository.findByCharacterName(characterName);
        if(character.isEmpty()){
            return Optional.empty();
        }
        return characterRepositoryMapper.characterToOptionalCharacterDto(character.get());
    }

    @Override
    @Transactional(readOnly = true)
    public CharacterDto readCharacterByUserId(int id){
        Character character = characterRepository.findByUserId(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user \"" + id + "\"");}
        );
        return characterRepositoryMapper.characterToCharacterDto(character);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CharacterDto> readCharacterList(){
        List<Character> characterList = characterRepository.findAll();
        return characterRepositoryMapper.characterListToCharacterDtoList(characterList);
    }

    @Override
    @Transactional
    public void updateCharacter(String targetName, CharacterDto changedCharacter){
        Character character = characterRepository.findByCharacterName(targetName).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find character \"" + targetName + "\"");}
        );
        character.setCharacterName(changedCharacter.getCharacterName());
        changedCharacter.getPermissionList().forEach(
                p -> character.getPermissionList().stream()
                        .filter(cp -> p.getId() == cp.getPermissionUnitId())
                        .findFirst()
                        .ifPresent(cp -> cp.setPermissionStatus(p.getStatus()))
        );
    }

    @Override
    @Transactional
    public void deleteCharacter(String characterName){
        Character character = characterRepository.findByCharacterName(characterName).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find character \"" + characterName + "\"");}
        );
        permissionRepository.deleteByCharacterId(character.getId());
        characterRepository.deleteById(character.getId());
    }

}
