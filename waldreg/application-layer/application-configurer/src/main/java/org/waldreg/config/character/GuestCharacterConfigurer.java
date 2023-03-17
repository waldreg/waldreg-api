package org.waldreg.config.character;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.exception.UnknownCharacterException;
import org.waldreg.character.management.CharacterManager;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.management.PermissionUnitListReadable;

@Component
public class GuestCharacterConfigurer{

    private final CharacterManager characterManager;
    private final PermissionUnitListReadable permissionUnitListReadable;
    private final ApplicationContext applicationContext;
    private final List<String> accessPermissionList;

    @Autowired
    public GuestCharacterConfigurer(CharacterManager characterManager,
            PermissionUnitListReadable permissionUnitListReadable,
            ApplicationContext applicationContext){
        this.characterManager = characterManager;
        this.permissionUnitListReadable = permissionUnitListReadable;
        this.applicationContext = applicationContext;
        this.accessPermissionList = List.of("Board read manager");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialGuestCharacter(){
        createGuestIfDoesNotExist();
        publish();
    }

    private void createGuestIfDoesNotExist(){
        try{
            characterManager.readCharacter("Guest");
        } catch (UnknownCharacterException UCE){
            characterManager.createCharacter(CharacterDto.builder()
                    .characterName("Guest")
                    .permissionDtoList(getGuestPermissionList())
                    .build());
        }
    }

    private List<PermissionDto> getGuestPermissionList(){
        List<PermissionUnit> permissionUnitList = permissionUnitListReadable.getPermissionUnitList();
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        for(PermissionUnit permissionUnit : permissionUnitList){
            PermissionDto permissionDto = PermissionDto.builder()
                    .service(permissionUnit.getService())
                    .id(permissionUnit.getId())
                    .name(permissionUnit.getName())
                    .status(accessPermissionList.contains(permissionUnit.getName())
                            ? getFailStatusOfPermissionUnit(permissionUnit, true)
                            : getFailStatusOfPermissionUnit(permissionUnit, false))
                    .build();
            permissionDtoList.add(permissionDto);
        }
        return permissionDtoList;
    }

    private String getFailStatusOfPermissionUnit(PermissionUnit permissionUnit, boolean expectedStatus){
        for(String status : permissionUnit.getStatusList()){
            if(permissionUnit.verify(status) == expectedStatus) return status;
        }
        throw new IllegalStateException("Can not find verify fail status of permission named \"" + permissionUnit.getName() + "\"");
    }

    private void publish(){
        applicationContext.publishEvent(new GuestCharacterCreatedEvent("created"));
    }


}
