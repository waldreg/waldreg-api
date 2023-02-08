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
        setBoardReadManager();
        setBoardModifyManager();
        setBoardDeleteManager();
        setCategoryManager();
        setCommentCreateManager();
        setCommentModifyManager();
        setCommentReadeManager();
        setCommentDeleteManager();
        setImageManager();
        setFileManager();
    }

    private void setBoardCreateManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board create manager")
                        .info("If set true, can create board")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setBoardReadManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board read manager")
                        .info("If set true, can read board")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setBoardModifyManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board modify manager")
                        .info("If set true, can modify board")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setBoardDeleteManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Board delete manager")
                        .info("If set true, can delete board")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setCategoryManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Category manager")
                        .info("If set true, all permissions related to the category are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setCommentCreateManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Comment create manager")
                        .info("If set true, can create comments.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setCommentReadeManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Comment read manager")
                        .info("If set true, can read comments.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setCommentModifyManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Comment modify manager")
                        .info("If set true, can modify comments.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setCommentDeleteManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Comment delete manager")
                        .info("If set true, can delete comments.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setImageManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Image manager")
                        .info("If set true, can load Image.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

    private void setFileManager(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("File download manager")
                        .info("If set true, can download file.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

}
