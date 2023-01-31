package org.waldreg.character.management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.NoPermissionException;
import org.waldreg.character.exception.UnknownPermissionException;
import org.waldreg.character.exception.UnknownPermissionStatusException;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.management.PermissionUnitListReadable;
import org.waldreg.character.spi.CharacterRepository;

@Service
public class DefaultCharacterManager implements CharacterManager{

    private final CharacterRepository characterRepository;
    private final PermissionChecker permissionChecker;
    private final PermissionUnitListReadable permissionUnitListReadable;
    private final String[] deletedBlockedPermissionNames = {"Admin", "Guest"};

    @Autowired
    private DefaultCharacterManager(CharacterRepository characterRepository,
            PermissionChecker permissionChecker,
            PermissionUnitListReadable permissionUnitListReadable){
        this.characterRepository = characterRepository;
        this.permissionChecker = permissionChecker;
        this.permissionUnitListReadable = permissionUnitListReadable;
    }

    @Override
    public void createCharacter(CharacterDto characterDto){
        throwIfInvalidPermissionDetected(characterDto.getPermissionList());
        CharacterDto filledCharacterDto = fillAbsentPermissionToCharacterDto(characterDto);
        characterRepository.createCharacter(filledCharacterDto);
    }

    @Override
    public void updateCharacter(String targetName, CharacterDto changedCharacter){
        throwIfInvalidPermissionDetected(changedCharacter.getPermissionList());
        if(isBlockedCharacterName(targetName)){
            throw new NoPermissionException();
        }
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
        if (!permissionChecker.isPossiblePermission(permissionDto.getId(), permissionDto.getName(), permissionDto.getStatus())){
            throw new UnknownPermissionStatusException(permissionDto.getId(), permissionDto.getName(), permissionDto.getStatus());
        }
    }

    private CharacterDto fillAbsentPermissionToCharacterDto(CharacterDto characterDto){
        Map<String, PermissionDto> permissionDtoMap = convertListToMap(characterDto.getPermissionList());
        fillAbsentPermissions(permissionDtoMap);
        return CharacterDto.builder()
                .characterName(characterDto.getCharacterName())
                .permissionDtoList(convertMapToList(permissionDtoMap))
                .build();
    }

    private Map<String, PermissionDto> convertListToMap(List<PermissionDto> permissionDtoList){
        Map<String, PermissionDto> permissionDtoMap = new HashMap<>();
        for(PermissionDto permissionDto : permissionDtoList){
            permissionDtoMap.put(permissionDto.getName(), permissionDto);
        }
        return permissionDtoMap;
    }

    private void fillAbsentPermissions(Map<String, PermissionDto> permissionDtoMap){
        List<PermissionUnit> permissionUnitList = permissionUnitListReadable.getPermissionUnitList();
        for(PermissionUnit permissionUnit : permissionUnitList){
            if(!permissionDtoMap.containsKey(permissionUnit.getName())){
                PermissionDto permissionDto = PermissionDto.builder()
                        .id(permissionUnit.getId())
                        .name(permissionUnit.getName())
                        .status(getStatusOfPermissionUnit(permissionUnit, false))
                        .build();
                permissionDtoMap.put(permissionUnit.getName(), permissionDto);
            }
        }
    }

    private String getStatusOfPermissionUnit(PermissionUnit permissionUnit, boolean targetStatus){
        for(String status : permissionUnit.getStatusList()){
            if(permissionUnit.verify(status) == targetStatus) return status;
        }
        throw new IllegalStateException("Can not find verifiable status of permission named \"" + permissionUnit.getName() + "\"");
    }

    private List<PermissionDto> convertMapToList(Map<String, PermissionDto> permissionDtoMap){
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        for(Map.Entry<String, PermissionDto> entry : permissionDtoMap.entrySet()){
            permissionDtoList.add(entry.getValue());
        }
        return permissionDtoList;
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
        if(isBlockedCharacterName(characterName)){
            throw new NoPermissionException();
        }
        characterRepository.deleteCharacter(characterName);
    }

    private boolean isBlockedCharacterName(String characterName){
        for(String name : deletedBlockedPermissionNames){
            if(name.equals(characterName)) return true;
        }
        return false;
    }

}
