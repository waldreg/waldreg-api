package org.waldreg.teambuilding.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class TeamBuildingPermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public TeamBuildingPermissionConfigurer(PermissionExtension permissionExtension){this.permissionExtension = permissionExtension;}

    @PostConstruct
    public void configTeamBuildingPermission(){
        configCreateNewTeamBuildingPermission();
        configUpdateTeamBuildingPermission();
        configDeleteTeamBuildingPermission();
    }

    private void configCreateNewTeamBuildingPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .name("TeamBuilding create manager")
                .info("If set true, can create new team building or team")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

    private void configUpdateTeamBuildingPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .name("TeamBuilding update manager")
                .info("If set true, can update team building or team")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

    private void configDeleteTeamBuildingPermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .name("TeamBuilding delete manager")
                .info("If set true, can delete team building or team")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

}
