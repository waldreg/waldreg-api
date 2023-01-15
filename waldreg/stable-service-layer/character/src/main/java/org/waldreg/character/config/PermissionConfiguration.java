package org.waldreg.character.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.extension.DefaultPermissionExtension;

@Configuration
public class PermissionConfiguration{

    private final DefaultPermissionExtension defaultPermissionExtension;

    @Autowired
    public PermissionConfiguration(DefaultPermissionExtension defaultPermissionExtension){
        this.defaultPermissionExtension = defaultPermissionExtension;
    }

    @PostConstruct
    public void extendCharacterManagerPermission(){
        PermissionUnit characterManagerPermission = DefaultPermissionUnit.builder()
                .name("Character Manager")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();
        defaultPermissionExtension.extend(characterManagerPermission);
    }

}
