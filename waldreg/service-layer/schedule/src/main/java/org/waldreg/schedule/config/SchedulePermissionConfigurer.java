package org.waldreg.schedule.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class SchedulePermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public SchedulePermissionConfigurer(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void configSchedulePermission(){
        configCreateNewSchedulePermission();
        configUpdateSchedulePermission();
        configDeleteSchedulePermission();
    }

    private void configCreateNewSchedulePermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("Schedule")
                .name("Schedule create manager")
                .info("If set true, can create new schedule")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

    private void configUpdateSchedulePermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("Schedule")
                .name("Schedule update manager")
                .info("If set true, can update schedule")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

    private void configDeleteSchedulePermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .service("Schedule")
                .name("Schedule delete manager")
                .info("If set true, can delete schedule")
                .permissionVerifiable((s) -> s.equals("true"))
                .statusList(List.of("true", "false"))
                .build());
    }

}
