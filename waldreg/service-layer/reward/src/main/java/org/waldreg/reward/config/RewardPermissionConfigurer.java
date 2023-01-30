package org.waldreg.reward.config;

import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.waldreg.character.permission.core.DefaultPermissionUnit;
import org.waldreg.character.permission.extension.PermissionExtension;

@Configuration
public class RewardPermissionConfigurer{

    private final PermissionExtension permissionExtension;

    @Autowired
    public RewardPermissionConfigurer(PermissionExtension permissionExtension){
        this.permissionExtension = permissionExtension;
    }

    @PostConstruct
    public void setUpRewardPermissions(){
        permissionExtension.extend(
                DefaultPermissionUnit.builder()
                        .name("Reward manager")
                        .info("If set true, all permissions related to the reward are accessible.")
                        .statusList(List.of("true", "false"))
                        .permissionVerifiable(s -> s.equals("true"))
                        .build()
        );
    }

}
