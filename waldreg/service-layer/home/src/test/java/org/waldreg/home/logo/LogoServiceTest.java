package org.waldreg.home.logo;

import java.io.FileInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.home.logo.management.DefaultLogoManager;
import org.waldreg.home.logo.management.LogoManager;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = {DefaultLogoManager.class})
public class LogoServiceTest{

    @Autowired
    LogoManager logoManager;

    @Test
    @DisplayName("로고 수정 성공 테스트")
    public void UPDATE_LOGO_SUCCESS_TEST() throws Exception{
        //given
        MultipartFile multipartFile = new MockMultipartFile("image","logo.svg","image/svg",new FileInputStream("./src/test/java/org/waldreg/home/logo/logo.svg"));

        //when&then
        Assertions.assertDoesNotThrow(() -> logoManager.updateLogo(multipartFile));

    }

    @Test
    @DisplayName("로고 조회 성공 테스트 - byte[]")
    public void READ_LOGO_SUCCESS_TEST() throws Exception{
        //given
        MultipartFile multipartFile = new MockMultipartFile("image","logo.svg","image/svg",new FileInputStream("./src/test/java/org/waldreg/home/logo/logo.svg"));

        //when
        logoManager.updateLogo(multipartFile);

        //then
        Assertions.assertDoesNotThrow(() -> logoManager.getLogo());

    }

}
