package org.waldreg.character.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.PermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class PermissionConfiguration{

    private final PermissionExtension permissionExtension;

    @Bean
    public void extendCharacterManagerPermission(){
        PermissionUnit characterManagerPermission = PermissionUnit.builder()
                .name("characterManagerPermission")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build();
        permissionExtension.extend(characterManagerPermission);
    }

    @Autowired
    public PermissionConfiguration(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

}
