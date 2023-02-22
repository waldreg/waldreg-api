package org.waldreg.controller.character.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.controller.character.request.CharacterRequest;
import org.waldreg.controller.character.response.CharacterResponse;
import org.waldreg.controller.character.response.SimpleCharacterResponse;

@Service
public class ControllerCharacterMapper{

    private final ControllerPermissionMapper controllerPermissionMapper;

    @Autowired
    public ControllerCharacterMapper(ControllerPermissionMapper controllerPermissionMapper){
        this.controllerPermissionMapper = controllerPermissionMapper;
    }

    public List<SimpleCharacterResponse> characterDtoListToSimpleCharacterResponseList(List<CharacterDto> characterDtoList){
        List<SimpleCharacterResponse> characterNameList = new ArrayList<>();
        for(CharacterDto characterDto : characterDtoList){
            characterNameList.add(SimpleCharacterResponse.builder()
                    .id(characterDto.getId())
                    .characterName(characterDto.getCharacterName())
                    .build());
        }
        return characterNameList;
    }

    public CharacterDto characterRequestToCharacterDto(CharacterRequest characterRequest){
        return CharacterDto.builder()
                .characterName(characterRequest.getCharacterName())
                .permissionDtoList(
                        controllerPermissionMapper.permissionRequestListToPermissionDtoList(characterRequest.getPermissionList())
                )
                .build();
    }

    public CharacterResponse characterDtoToCharacterResponse(CharacterDto characterDto){
        return CharacterResponse.builder()
                .id(characterDto.getId())
                .characterName(characterDto.getCharacterName())
                .permissionList(controllerPermissionMapper.permissionDtoListToSpecifyPermissionResponseList(characterDto.getPermissionList()))
                .build();
    }

}
