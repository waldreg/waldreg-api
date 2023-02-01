package org.waldreg.board.board;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.DefaultBoardManager;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultBoardManager.class})
public class BoardManagerTest{

    @Autowired
    private BoardManager boardManager;

    @MockBean
    private BoardRepository boardRepository;

    @Test
    @DisplayName("보드 생성 성공 테스트")
    public void CREATE_BOARD_SUCCESS_TEST(){

        //given
        String title = "title";
        String content = "content";

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
        ReactionDto reactionDto = ReactionDto.builder().build();

        BoardDto boardDto = BoardDto.builder()
                .user(userDto)
                .title(title)
                .content(content)
                .category(categoryDto)
                .reactions(reactionDto)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> boardManager.createBoard(boardDto));
    }

}
