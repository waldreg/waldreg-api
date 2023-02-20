package org.waldreg.character.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class PermissionConfiguration{

    private final PermissionExtension permissionExtension;

    @Autowired
    public PermissionConfiguration(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void extendCharacterManagerPermission(){
        PermissionUnit characterManagerPermission = DefaultPermissionUnit.builder()
                .service("Character")
                .name("Character manager")
                .info("If set true, Permission to edit, delete, or create new characters.")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();
        permissionExtension.extend(characterManagerPermission);
    }

}
