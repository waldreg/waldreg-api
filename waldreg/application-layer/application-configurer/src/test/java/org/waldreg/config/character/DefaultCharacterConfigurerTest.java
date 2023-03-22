package org.waldreg.config.character;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.waldreg.character.dto.CharacterDto;
import org.waldreg.character.dto.PermissionDto;
import org.waldreg.character.management.CharacterManager;
import org.waldreg.character.permission.verification.PermissionVerifier;

@SpringBootTest
public class DefaultCharacterConfigurerTest{

    @Autowired
    private CharacterManager characterManager;

    @Autowired
    private PermissionVerifier permissionVerifier;

    @Test
    @DisplayName("애플리케이션 시작시 \"Admin\" character 추가 확인 테스트")
    public void ADMIN_CHARACTER_SET_UP_TEST(){
        // given
        String adminName = "Admin";

        // when
        CharacterDto characterDto = characterManager.readCharacter(adminName);

        // then
        Assertions.assertAll(
                ()-> Assertions.assertEquals(adminName, characterDto.getCharacterName()),
                ()-> Assertions.assertTrue(isAllPermissionVerified(characterDto.getPermissionList()))
        );
    }

    private boolean isAllPermissionVerified(List<PermissionDto> permissionDtoList){
        for(PermissionDto permissionDto : permissionDtoList){
            if(!permissionVerifier.verify(permissionDto.getName(), permissionDto.getStatus())) return false;
        }
        return true;
    }

    @Test
    @DisplayName("애플리케이션 시작시 \"Guest\" character 추가 확인 테스트")
    public void GUEST_CHARACTER_SET_UP_TEST(){
        // given
        String guestName = "Guest";

        // when
        CharacterDto characterDto = characterManager.readCharacter(guestName);

        // then
        Assertions.assertAll(
                ()-> Assertions.assertEquals(guestName, characterDto.getCharacterName()),
                ()-> Assertions.assertTrue(isAllPermissionFailVerifying(characterDto.getPermissionList()))
        );
    }

    private boolean isAllPermissionFailVerifying(List<PermissionDto> permissionDtoList){
        for(PermissionDto permissionDto : permissionDtoList){
            if(permissionVerifier.verify(permissionDto.getName(), permissionDto.getStatus())) return false;
        }
        return true;
    }

}
