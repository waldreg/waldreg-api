package org.waldreg.board.board;


import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.DefaultBoardManager;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.MemberTier;
import org.waldreg.board.dto.UserDto;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultBoardManager.class})
public class BoardManagerTest{

    @Autowired
    private BoardManager boardManager;

    @Test
    @DisplayName("보드 생성 성공 테스트")
    public void CREATE_BOARD_SUCCESS_TEST(){

        //given
        String title = "title";
        String content = "content";
        List<String> imageList = new ArrayList<>();
        imageList.add("./image/1");
        List<String> fileList = new ArrayList<>();
        imageList.add("./file/2");

        int id = 1;
        String name = "name";
        String userId = "id";
        UserDto userDto = UserDto.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .build();

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("cate1")
                .build();

        BoardDto boardDto = BoardDto.builder()
                .user(userDto)
                .title(title)
                .content(content)
                .category(categoryDto)
                .imagePathList(imageList)
                .filePathList(fileList)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> boardManager.createBoard(boardDto));
    }

    @Test
    @DisplayName("보드 생성 실패 테스트 - 제목이 비어있을 경우")
    public void CREATE_BOARD_SUCCESS_TEST(){

        //given
        String title = "";
        String content = "content";
        List<String> imageList = new ArrayList<>();
        imageList.add("./image/1");

        List<String> fileList = new ArrayList<>();
        imageList.add("./file/2");

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("cate1")
                .memberTier(MemberTier.TIER_3)
                .build();

        int id = 1;
        String name = "name";
        String userId = "id";
        UserDto userDto = UserDto.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .user(userDto)
                .title(title)
                .content(content)
                .category(categoryDto)
                .imagePathList(imageList)
                .filePathList(fileList)
                .build();

        //when&then
        Assertions.assertThrows(BlankTitleException.class, () -> boardManager.createBoard(boardDto));
    }



}
