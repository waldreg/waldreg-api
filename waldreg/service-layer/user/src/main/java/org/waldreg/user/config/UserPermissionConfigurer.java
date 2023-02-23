package org.waldreg.user.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class UserPermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public UserPermissionConfigurer(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void configUserPermission(){
        configFireOtherUserPermission();
        configUpdateUserCharacterPermission();
        configReadOtherUserPermission();
        configJoinUserPermission();
    }

    private void configFireOtherUserPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("User")
                .name("User fire manager")
                .info("If set true, can fire other user")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

    private void configUpdateUserCharacterPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("User")
                .name("User's character update manager")
                .info("If set true, can update user's character")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

    private void configReadOtherUserPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("User")
                .name("User info read manager")
                .info("If set true, can read other user's information")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }
    private void configJoinUserPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("User")
                .name("User join manager")
                .info("If set true, can handle other user's join request")
                .permissionVerifiable(s -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

}
