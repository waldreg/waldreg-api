package org.waldreg.controller.character;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.waldreg.character.aop.annotation.PermissionVerifying;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.management.CharacterManager;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.management.PermissionUnitListReadable;
import org.waldreg.controller.character.mapper.ControllerCharacterMapper;
import org.waldreg.controller.character.mapper.ControllerPermissionMapper;
import org.waldreg.controller.character.request.CharacterRequest;
import org.waldreg.controller.character.response.CharacterResponse;
import org.waldreg.controller.character.response.PermissionResponse;
import org.waldreg.controller.character.response.SimpleCharacterResponse;
import org.waldreg.token.aop.annotation.Authenticating;

@RestController
public class CharacterController{

    private final CharacterManager characterManager;
    private final PermissionUnitListReadable permissionUnitListReadable;
    private final ControllerPermissionMapper controllerPermissionMapper;
    private final ControllerCharacterMapper controllerCharacterMapper;

    @Autowired
    public CharacterController(CharacterManager characterManager,
            PermissionUnitListReadable permissionUnitListReadable,
            ControllerPermissionMapper controllerPermissionMapper,
            ControllerCharacterMapper controllerCharacterMapper){
        this.characterManager = characterManager;
        this.permissionUnitListReadable = permissionUnitListReadable;
        this.controllerPermissionMapper = controllerPermissionMapper;
        this.controllerCharacterMapper = controllerCharacterMapper;
    }

    @Authenticating
    @PermissionVerifying("Character manager")
    @GetMapping("/permission")
    public Map<String, List<PermissionResponse>> getAllPermissions(){
        List<PermissionUnit> permissionUnits = permissionUnitListReadable.getPermissionUnitList();
        List<PermissionResponse> permissionResponseList = controllerPermissionMapper.permissionUnitToPermissionResponse(permissionUnits);
        return Map.of("permissions", permissionResponseList);
    }

    @Authenticating
    @PermissionVerifying("Character manager")
    @GetMapping("/character")
    public Map<String, List<SimpleCharacterResponse>> getAllCharacters(){
        List<SimpleCharacterResponse> simpleCharacterResponseList = controllerCharacterMapper.characterDtoListToSimpleCharacterResponseList(characterManager.readCharacterList());
        return Map.of("characters", simpleCharacterResponseList);
    }

    @Authenticating
    @PermissionVerifying("Character manager")
    @PostMapping("/character")
    public void createNewCharacter(@RequestBody @Validated CharacterRequest characterRequest){
        CharacterDto characterDto = controllerCharacterMapper.characterRequestToCharacterDto(characterRequest);
        characterManager.createCharacter(characterDto);
    }

    @Authenticating
    @PermissionVerifying("Character manager")
    @GetMapping("/character/{character-name}")
    public CharacterResponse getCharacterByName(@PathVariable("character-name") String characterName){
        CharacterDto characterDto = characterManager.readCharacter(characterName);
        return controllerCharacterMapper.characterDtoToCharacterResponse(characterDto);
    }

    @Authenticating
    @PermissionVerifying("Character manager")
    @PatchMapping("/character/{character-name}")
    public void updateCharacter(@PathVariable("character-name") String characterName, @RequestBody CharacterRequest characterRequest){
        characterManager.updateCharacter(characterName, controllerCharacterMapper.characterRequestToCharacterDto(characterRequest));
    }

    @Authenticating
    @PermissionVerifying("Character manager")
    @DeleteMapping("/character/{character-name}")
    public void deleteCharacter(@PathVariable("character-name") String characterName){
        characterManager.deleteCharacter(characterName);
    }

}
