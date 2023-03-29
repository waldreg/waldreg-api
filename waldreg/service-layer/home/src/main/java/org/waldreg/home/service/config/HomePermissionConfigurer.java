package org.waldreg.home.service.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class HomePermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public HomePermissionConfigurer(PermissionExtension permissionExtension){this.permissionExtension = permissionExtension;}

    @PostConstruct
    public void configUpdateHomeContentPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("Home")
                .name("Home Content Update manager")
                .info("If set true, can update home content")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

}
