package org.waldreg.config.character;

import java.util.ArrayList;
import java.util.List;
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
public class AdminCharacterConfigurer{

    private final CharacterManager characterManager;
    private final PermissionUnitListReadable permissionUnitListReadable;
    private final ApplicationContext applicationContext;

    public AdminCharacterConfigurer(CharacterManager characterManager,
            PermissionUnitListReadable permissionUnitListReadable,
            ApplicationContext applicationContext){
        this.characterManager = characterManager;
        this.permissionUnitListReadable = permissionUnitListReadable;
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initialAdminCharacter(){
        createAdminIfDoesNotExist();
        publish();
    }

    private void createAdminIfDoesNotExist(){
        try{
            characterManager.readCharacter("Admin");
        } catch (UnknownCharacterException UCE){
            characterManager.createCharacter(CharacterDto.builder()
                    .characterName("Admin")
                    .permissionDtoList(getSuperPermissionList())
                    .build());
        }
    }

    private List<PermissionDto> getSuperPermissionList(){
        List<PermissionUnit> permissionUnitList = permissionUnitListReadable.getPermissionUnitList();
        List<PermissionDto> permissionDtoList = new ArrayList<>();
        for(PermissionUnit permissionUnit : permissionUnitList){
            PermissionDto permissionDto = PermissionDto.builder()
                    .name(permissionUnit.getName())
                    .status(getSuccessStatusOfPermissionUnit(permissionUnit))
                    .build();
            permissionDtoList.add(permissionDto);
        }
        return permissionDtoList;
    }

    private String getSuccessStatusOfPermissionUnit(PermissionUnit permissionUnit){
        for(String status : permissionUnit.getStatusList()){
            if(permissionUnit.verify(status)) return status;
        }
        throw new IllegalStateException("Can not find verifiable status of permission named \"" + permissionUnit.getName() + "\"");
    }

    private void publish(){
        applicationContext.publishEvent(new AdminCharacterCreatedEvent("created"));
    }

}
