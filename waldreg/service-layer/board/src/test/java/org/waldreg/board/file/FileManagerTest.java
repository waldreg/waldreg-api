package org.waldreg.board.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = DefaultFileManager.class)
public class FileManagerTest{

    @Autowired
    private FileManager fileManager;

    @Test
    @DisplayName("파일 저장 테스트")
    public void CREATE_FILE_SUCCESS_TEST() throws IOException, ExecutionException, InterruptedException{
        // given
        MultipartFile multipartFile = new MockMultipartFile("image",
                "EGG.png",
                "image/png",
                new FileInputStream("./src/test/java/org/waldreg/board/file/EGG.png"));

        // when
        Future<String> future = fileManager.saveFile(multipartFile);

        // then
        Assertions.assertNotNull(future.get());
    }

    @Test
    @DisplayName("파일 이름 변경 테스트")
    public void RENAME_FILE_SUCCESS_TEST() throws Exception{
        // given
        String id = "1.png";
        MultipartFile multipartFile = new MockMultipartFile("image",
                "EGG.png",
                "image/png",
                new FileInputStream("./src/test/java/org/waldreg/board/file/EGG.png"));

        // when
        Future<String> future = fileManager.saveFile(multipartFile);

        // then
        Assertions.assertTrue(fileManager.renameFile(future.get(), id).get());
    }

}
