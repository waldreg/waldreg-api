package org.waldreg.attendance.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class AttendanceConfig{

    private final PermissionExtension permissionExtension;

    @Autowired
    public AttendanceConfig(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void extendAttendancePermission(){
        permissionExtension.extend(DefaultPermissionUnit.builder()
                .name("Attendance manager")
                .info("If set to true, you can start and manage attendance.")
                .statusList(List.of("true", "false"))
                .permissionVerifiable(s -> s.equals("true"))
                .build());
    }

}
