package org.waldreg.board.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class BoardPermissionConfigurer{


    private final PermissionExtension permissionExtension;

    @Autowired
    public BoardPermissionConfigurer(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void setUpBoardPermissions(){
        setBoardCreateManager();
        setBoardModifyManager();
        setBoardDeleteManager();
        setCategoryManager();
        setCommentManager();
        setFileManager();
    }

    private void setBoardCreateManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board create manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }
    private void setBoardModifyManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board modify manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setBoardDeleteManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board delete manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setCategoryManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Category manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }
    private void setCommentManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Comment manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }
    private void setFileManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("File download manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

}
