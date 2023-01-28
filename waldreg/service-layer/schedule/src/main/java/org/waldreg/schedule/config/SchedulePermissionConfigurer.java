package org.waldreg.schedule.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

public class SchedulePermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public SchedulePermissionConfigurer(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void configSchedulePermission(){
        configCreateNewSchedulePermission();
    }

    private void configCreateNewSchedulePermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .name("Create new schedule")
                .info("If set true, can create new schedule")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }


}
