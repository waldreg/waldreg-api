package org.waldreg.home.service.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class ColorPermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public ColorPermissionConfigurer(PermissionExtension permissionExtension){this.permissionExtension = permissionExtension;}

    @PostConstruct
    public void configUpdateColorPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("Color")
                .name("Application color Update manager")
                .info("If set true, can update application color")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

}
