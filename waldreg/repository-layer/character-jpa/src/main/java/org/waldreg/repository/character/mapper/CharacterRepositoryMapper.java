package org.waldreg.repository.character.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;

@Component
public class CharacterRepositoryMapper{

    public Character characterDtoToCharacter(CharacterDto characterDto){
        return Character.builder()
                .characterName(characterDto.getCharacterName())
                .permissionList(
                        characterDto.getPermissionList().stream()
                                .map(cdp -> Permission.builder()
                                        .service(cdp.getService())
                                        .permissionUnitId(cdp.getId())
                                        .name(cdp.getName())
                                        .status(cdp.getStatus())
                                        .build())
                                .collect(Collectors.toList())
                ).build();
    }

    public Optional<CharacterDto> characterToOptionalCharacterDto(Character character){
        return Optional.of(characterToCharacterDto(character));
    }

    public CharacterDto characterToCharacterDto(Character character){
        return CharacterDto.builder()
                .id(character.getId())
                .characterName(character.getCharacterName())
                .permissionDtoList(
                        character.getPermissionList().stream()
                                .map(cp -> PermissionDto.builder()
                                        .id(cp.getPermissionUnitId())
                                        .service(cp.getService())
                                        .name(cp.getName())
                                        .status(cp.getStatus())
                                        .build())
                                .collect(Collectors.toList())
                ).build();
    }

    public List<CharacterDto> characterListToCharacterDtoList(List<Character> characterList){
        return characterList.stream().map(
                c -> CharacterDto.builder()
                        .id(c.getId())
                        .characterName(c.getCharacterName())
                        .permissionDtoList(
                                c.getPermissionList().stream().map(
                                        cp -> PermissionDto.builder()
                                                .id(cp.getPermissionUnitId())
                                                .name(cp.getName())
                                                .status(cp.getStatus())
                                                .service(cp.getService())
                                                .build()
                                ).collect(Collectors.toList())
                        ).build()
        ).collect(Collectors.toList());
    }

}
