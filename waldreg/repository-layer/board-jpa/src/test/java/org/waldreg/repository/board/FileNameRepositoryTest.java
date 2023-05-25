package org.waldreg.repository.board;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ContextConfiguration(classes = {FileNameCommander.class, JpaBoardTestInitializer.class})
@TestPropertySource("classpath:h2-application.properties")
public class FileNameRepositoryTest{

    @Autowired
    private FileNameCommander fileNameCommander;


    @Test
    @DisplayName("파일 이름, uuid 저장, 조회 테스트")
    void SAVE_FILENAME_TEST(){
        //given
        String origin = "origin.pptx";
        String uuid = "abasce-dacdscas-dascaasd-asvadv.pptx";
        //when
        fileNameCommander.saveFileName(origin,uuid);
        String result = fileNameCommander.getUUIDByOrigin(origin);

        //then
        Assertions.assertEquals(result, uuid);
    }

}
