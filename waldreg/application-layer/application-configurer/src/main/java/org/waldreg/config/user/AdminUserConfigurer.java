package org.waldreg.config.user;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.waldreg.config.character.AdminCharacterConfigurer;
import org.waldreg.config.character.AdminCharacterCreatedEvent;
import org.waldreg.user.dto.UserDto;
import org.waldreg.user.exception.UnknownUserIdException;
import org.waldreg.user.management.UserManager;

@Component
public class AdminUserConfigurer{

    private final UserManager userManager;
    private final String adminUserId;

    @Autowired
    public AdminUserConfigurer(UserManager userManager){
        this.userManager = userManager;
        this.adminUserId = "Admin";
    }

    @EventListener(AdminCharacterCreatedEvent.class)
    public void initialAdminUser(){
        System.out.println("\n\n\n initialAdminUser() \n\n\n");
        try{
            userManager.readUserByUserId(adminUserId);
        } catch (UnknownUserIdException UUIE){
            userManager.createUser(getAdminUser());
        }
    }

    private UserDto getAdminUser(){
        return UserDto.builder()
                .name("Admin")
                .userId(adminUserId)
                .userPassword("0000")
                .character("Admin")
                .phoneNumber("000-0000-0000")
                .socialLogin(List.of())
                .build();
    }

}
