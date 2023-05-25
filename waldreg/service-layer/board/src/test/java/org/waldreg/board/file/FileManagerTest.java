package org.waldreg.board.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.multipart.MultipartFile;
import org.waldreg.board.file.data.FileData;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = {DefaultFileManager.class, FileData.class})
@WebAppConfiguration
public class FileManagerTest{

    @Autowired
    private FileManager fileManager;

    private final Queue<String> deleteQueue = new LinkedList<>();

    @Autowired
    private FileData fileData;

    @MockBean
    private BoardFileNameRepository boardFileNameRepository;

    @BeforeEach
    @AfterEach
    public void DELETE_FILE(){
        while(!deleteQueue.isEmpty()){
            String target = deleteQueue.poll();
            fileManager.deleteFile(target);
        }
    }

    @Test
    @DisplayName("파일 저장 테스트")
    public void CREATE_FILE_SUCCESS_TEST() throws IOException, ExecutionException, InterruptedException{
        // given
        MultipartFile multipartFile = new MockMultipartFile("image",
                "EGG.png",
                "image/png",
                new FileInputStream("./src/test/java/org/waldreg/board/file/EGG.png"));

        // when
        fileManager.saveFile(multipartFile);
        String name = fileData.getSavedImageNameList().get(0).get();
        deleteQueue.add(name);

        // then
        Assertions.assertNotNull(name);
    }

    @Test
    @DisplayName("파일 조회 테스트 - byte[]")
    public void READ_FILE_SUCCESS_BYTE_TEST() throws Exception{
        // given
        MultipartFile multipartFile = new MockMultipartFile("image",
                "EGG.png",
                "image/png",
                new FileInputStream("./src/test/java/org/waldreg/board/file/EGG.png"));

        // when
        fileManager.saveFile(multipartFile);

        //String name = boardFileNameRepository.getUUIDByOrigin(fileData.getSavedImageNameList().get(0).get());
        String name = fileData.getSavedImageNameList().get(0).get();
        System.out.println("@@ name : " + name);
        deleteQueue.add(name);

        //이름으로 조회를 해야하는데 mockbean으로 레포를 해둬서 이름에 매칭되는 uuid 값을 가져올수가 없어서 테스트가 안돼.

        // then
        Assertions.assertDoesNotThrow(() -> fileManager.getFileIntoByteArray(name));
    }

    @Test
    @DisplayName("파일 조회 테스트 - File")
    public void READ_FILE_SUCCESS_FILE_TEST() throws Exception{
        // given
        MultipartFile multipartFile = new MockMultipartFile("image",
                "EGG.png",
                "image/png",
                new FileInputStream("./src/test/java/org/waldreg/board/file/EGG.png"));

        // when
        fileManager.saveFile(multipartFile);
        String name = fileData.getSavedImageNameList().get(0).get();
        deleteQueue.add(name);

        // then
        Assertions.assertDoesNotThrow(() -> fileManager.getFileIntoFile(name));
    }

}
