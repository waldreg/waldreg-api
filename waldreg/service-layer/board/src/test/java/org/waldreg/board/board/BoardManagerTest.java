package org.waldreg.board.board;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.BoardManager.BoardRequest;
import org.waldreg.board.board.management.DefaultBoardManager;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.board.spi.CategoryRepository;
import org.waldreg.board.board.spi.UserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.MemberTier;
import org.waldreg.board.dto.UserDto;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultBoardManager.class})
public class BoardManagerTest{

    @Autowired
    private BoardManager boardManager;

    @MockBean
    private BoardRepository boardRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private CategoryRepository categoryRepository;


    @Test
    @DisplayName("보드 생성 성공 테스트")
    public void CREATE_BOARD_SUCCESS_TEST(){

        //given
        String title = "title";
        String content = "content";

        int id = 1;
        int categoryId = 1;
        MemberTier memberTier = MemberTier.TIER_3;

        Mockito.when(userRepository.isExistUser(Mockito.anyInt())).thenReturn(true);
        Mockito.when(categoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);

        BoardRequest boardRequest = BoardRequest.builder()
                .authorId(id)
                .title(title)
                .content(content)
                .categoryId(categoryId)
                .memberTier(memberTier)
                .imageCount(2)
                .fileCount(3)
                .build();

        //when&then
        Assertions.assertDoesNotThrow(() -> boardManager.createBoard(boardRequest));
    }

    @Test
    @DisplayName("아이디로 게시글 조회 성공 테스트")
    public void INQUIRY_BOARD_BY_ID_TEST(){
        //given
        String title = "title";
        String content = "content";

        int id = 1;
        int categoryId = 1;
        MemberTier memberTier = MemberTier.TIER_3;

        UserDto userDto = UserDto.builder()
                .id(id)
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .id(categoryId)
                .categoryName("categoryName")
                .build();

        BoardDto boardDto = BoardDto.builder()
                .id(1)
                .user(userDto)
                .category(categoryDto)
                .memberTier(memberTier)
                .title(title)
                .content(content)
                .build();

        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.inquiryBoardById(Mockito.anyInt())).thenReturn(boardDto);

        //when
        BoardDto result = boardManager.inquiryBoardById(1);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto.getId(),result.getId()),
                () -> Assertions.assertEquals(boardDto.getTitle(),result.getTitle()),
                () -> Assertions.assertEquals(boardDto.getContent(),result.getContent()),
                () -> Assertions.assertEquals(boardDto.getCategory(),result.getCategory()),
                () -> Assertions.assertEquals(boardDto.getMemberTier(),result.getMemberTier()),
                () -> Assertions.assertEquals(boardDto.getUser(),result.getUser())
        );

    }

}
