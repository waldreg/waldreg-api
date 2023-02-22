package org.waldreg.repository.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.spi.CharacterRepository;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryCharacterStorage;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.spi.UsersCharacterRepository;

@Repository
public class MemoryCharacterRepository implements CharacterRepository, UsersCharacterRepository{

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
        memoryCharacterStorage.createCharacter(
                characterMapper.characterDtoToDomain(characterDto)
        );
    }

    @Override
    public void updateCharacter(String targetName, CharacterDto changedCharacter){
        Character beforeCharacter = memoryCharacterStorage.readCharacterByName(targetName);
        List<Permission> changedPermissionList = getChangedPermissionList(beforeCharacter.getPermissionList(),
                characterMapper.characterDtoToDomain(changedCharacter).getPermissionList());

        memoryCharacterStorage.createCharacter(Character.builder()
                .characterName(changedCharacter.getCharacterName())
                .permissionList(changedPermissionList)
                .build());
    }

    private List<Permission> getChangedPermissionList(List<Permission> beforePermissionList, List<Permission> afterPermissionList){
        Map<String, Permission> changedPermissionMap = new HashMap<>();
        setChangedPermission(changedPermissionMap, afterPermissionList);
        setChangedPermission(changedPermissionMap, beforePermissionList);
        return permissionMapToList(changedPermissionMap);
    }

    private void setChangedPermission(Map<String, Permission> changedPermissionMap, List<Permission> permissionList){
        for(Permission permission : permissionList){
            if(changedPermissionMap.containsKey(permission.getName())){
                continue;
            }
            changedPermissionMap.put(permission.getName(), permission);
        }
    }

    private List<Permission> permissionMapToList(Map<String, Permission> permissionMap){
        List<Permission> permissionList = new ArrayList<>();
        for(Map.Entry<String, Permission> permission : permissionMap.entrySet()){
            permissionList.add(permission.getValue());
        }
        return permissionList;
    }

    @Override
    public Optional<CharacterDto> readCharacter(String characterName){
        Character character = memoryCharacterStorage.readCharacterByName(characterName);
        if(character == null){
            return Optional.empty();
        }
        return Optional.of(characterMapper.characterDomainToDto(character));
    }

    @Override
    public CharacterDto readCharacterByUserId(int id){
        User user = memoryUserStorage.readUserById(id);
        return characterMapper.characterDomainToDto(user.getCharacter());
    }

    @Override
    public void deleteCharacter(String characterName){
        memoryCharacterStorage.deleteCharacterByName(characterName);
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
    public boolean isExistCharacterName(String characterName){
        return memoryCharacterStorage.isExistCharacterName(characterName);
    }

}