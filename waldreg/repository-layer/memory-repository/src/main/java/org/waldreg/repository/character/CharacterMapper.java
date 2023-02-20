package org.waldreg.repository.character;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;

@Service
public class CharacterMapper{

    public Character characterDtoToDomain(CharacterDto characterDto){
        return Character.builder()
                .characterName(characterDto.getCharacterName())
                .permissionList(permissionDtoToDomain(characterDto.getPermissionList()))
                .build();
    }

    private List<Permission> permissionDtoToDomain(List<PermissionDto> permissionDtoList){
        List<Permission> permissionList = new ArrayList<>();
        for (PermissionDto permissionDto : permissionDtoList){
            permissionList.add(Permission.builder()
                    .id(permissionDto.getId())
                    .service(permissionDto.getService())
                    .name(permissionDto.getName())
                    .status(permissionDto.getStatus())
                    .build());
        }
        return permissionList;
    }

    public CharacterDto characterDomainToDto(Character character){
        return CharacterDto.builder()
                .id(character.getId())
                .characterName(character.getCharacterName())
                .permissionDtoList(permissionDomainToDto(character.getPermissionList()))
                .build();
    }

    private List<PermissionDto> permissionDomainToDto(List<Permission> permissionList){
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        for (Permission permission : permissionList){
            permissionDtoList.add(PermissionDto.builder()
                    .id(permission.getId())
                    .service(permission.getService())
                    .name(permission.getName())
                    .status(permission.getStatus())
                    .build());
        }
        return permissionDtoList;
    }

}
