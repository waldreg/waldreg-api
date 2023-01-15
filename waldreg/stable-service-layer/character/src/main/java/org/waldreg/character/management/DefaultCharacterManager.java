package org.waldreg.character.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.exception.UnknownPermissionStatusException;
import org.waldreg.character.spi.CharacterRepository;

@Service
public class DefaultCharacterManager implements CharacterManager{

    private final CharacterRepository characterRepository;
    private final PermissionChecker permissionChecker;

    @Override
    public void createCharacter(CharacterDto characterDto){
        throwIfInvalidPermissionDetected(characterDto.getPermissionList());
        characterRepository.createCharacter(characterDto);
    }

    @Override
    public void updateCharacter(String targetName, CharacterDto changedCharacter){
        throwIfInvalidPermissionDetected(changedCharacter.getPermissionList());
        characterRepository.updateCharacter(targetName, changedCharacter);
    }

    private void throwIfInvalidPermissionDetected(List<PermissionDto> permissionDtoList){
        for (PermissionDto permissionDto : permissionDtoList){
            throwIfInvalidPermissionNameDetected(permissionDto);
            throwIfInvalidPermissionStatusDetected(permissionDto);
        }
    }

    private void throwIfInvalidPermissionNameDetected(PermissionDto permissionDto){
        if (!permissionChecker.isPermissionExist(permissionDto.getName())){
            throw new UnknownPermissionException(permissionDto.getName());
        }
    }

    private void throwIfInvalidPermissionStatusDetected(PermissionDto permissionDto){
        if (!permissionChecker.isPossiblePermissionStatus(permissionDto.getName(), permissionDto.getStatus())){
            throw new UnknownPermissionStatusException(permissionDto.getName(), permissionDto.getStatus());
        }
    }

    @Override
    public CharacterDto readCharacter(String characterName){
        return characterRepository.readCharacter(characterName);
    }

    @Override
    public CharacterDto readCharacterByUserId(int id){
        return characterRepository.readCharacterByUserId(id);
    }

    @Override
    public List<CharacterDto> readCharacterList(){
        return characterRepository.readCharacterList();
    }

    @Override
    public void deleteCharacter(String characterName){
        characterRepository.deleteCharacter(characterName);
    }

    @Autowired
    private DefaultCharacterManager(CharacterRepository characterRepository, PermissionChecker permissionChecker){
        this.characterRepository = characterRepository;
        this.permissionChecker = permissionChecker;
    }

}
