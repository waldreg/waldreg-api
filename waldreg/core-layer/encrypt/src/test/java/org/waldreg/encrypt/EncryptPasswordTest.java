package org.waldreg.encrypt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultEncryptPassword.class})
public class EncryptPasswordTest{


    @Autowired
    private EncryptPassword encrypt;

    @Test
    @DisplayName("랜덤한 salt값 생성 테스트")
    public void CREATE_RANDOM_SALT_VALUE(){
        //given
        String salt = encrypt.createSalt();
        //when & then
        Assertions.assertNotNull(salt);
    }

    @Test
    @DisplayName("암호화된 비밀번호 생성 성공 테스트")
    public void GET_ENCRYPTED_PASSWORD_TEST(){
        //given
        String salt = encrypt.createSalt();
        String password = "Aasdf@";

        //when
        String encryptedPassword = encrypt.getEncryptPassword(password,salt);

        //then
        Assertions.assertNotNull(encryptedPassword);
    }

}
