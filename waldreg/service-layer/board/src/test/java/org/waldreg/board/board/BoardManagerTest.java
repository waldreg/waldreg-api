package org.waldreg.board.board;


import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.board.exception.BoardDoesNotExistException;
import org.waldreg.board.board.exception.InvalidRangeException;
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
                () -> Assertions.assertEquals(boardDto.getId(), result.getId()),
                () -> Assertions.assertEquals(boardDto.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardDto.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardDto.getCategory(), result.getCategory()),
                () -> Assertions.assertEquals(boardDto.getMemberTier(), result.getMemberTier()),
                () -> Assertions.assertEquals(boardDto.getUser(), result.getUser())
        );

    }

    @Test
    @DisplayName("아이디로 게시글 조회 실패 테스트")
    public void INQUIRY_BOARD_BY_ID_FAIL_TEST(){
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

        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(false);
        Mockito.when(boardRepository.inquiryBoardById(Mockito.anyInt())).thenReturn(boardDto);

        //when&then
        Assertions.assertThrows(BoardDoesNotExistException.class, () -> boardManager.inquiryBoardById(1));
    }


    @Test
    @DisplayName("전체 게시글 조회 성공")
    public void INQUIRY_ALL_BOARD_SUCCESS_TEST(){

        //given
        String title = "title";
        String content = "content";

        int id1 = 1;
        int id2 = 2;
        int categoryId1 = 1;
        int categoryId2 = 2;

        MemberTier memberTier = MemberTier.TIER_3;

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(id2)
                .build();
        CategoryDto categoryDto1 = CategoryDto.builder()
                .id(categoryId1)
                .categoryName("categoryName1")
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(categoryId2)
                .categoryName("categoryName2")
                .build();

        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .user(userDto1)
                .category(categoryDto1)
                .memberTier(memberTier)
                .title(title)
                .content(content)
                .build();
        BoardDto boardDto2 = BoardDto.builder()
                .id(1)
                .user(userDto2)
                .category(categoryDto2)
                .memberTier(memberTier)
                .title("title2")
                .content("content2")
                .build();
        List<BoardDto> result = new ArrayList<>();
        result.add(boardDto1);
        result.add(boardDto2);

        //when
        Mockito.when(boardRepository.inquiryAllBoard(Mockito.anyInt(), Mockito.anyInt())).thenReturn(result);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto1.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(boardDto1.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardDto1.getCategory(), result.get(0).getCategory()),
                () -> Assertions.assertEquals(boardDto1.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardDto2.getId(), result.get(1).getId()),
                () -> Assertions.assertEquals(boardDto2.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardDto2.getCategory(), result.get(1).getCategory()),
                () -> Assertions.assertEquals(boardDto2.getContent(), result.get(1).getContent()));

    }


    @Test
    @DisplayName("전체 게시글 조회 실패 - 올바르지 않은 범위")
    public void INQUIRY_ALL_BOARD_INVALID_RANGE_TEST(){

        //given
        String title = "title";
        String content = "content";

        int id1 = 1;
        int id2 = 2;
        int categoryId1 = 1;
        int categoryId2 = 2;

        MemberTier memberTier = MemberTier.TIER_3;

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(id2)
                .build();
        CategoryDto categoryDto1 = CategoryDto.builder()
                .id(categoryId1)
                .categoryName("categoryName1")
                .build();
        CategoryDto categoryDto2 = CategoryDto.builder()
                .id(categoryId2)
                .categoryName("categoryName2")
                .build();

        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .user(userDto1)
                .category(categoryDto1)
                .memberTier(memberTier)
                .title(title)
                .content(content)
                .build();
        BoardDto boardDto2 = BoardDto.builder()
                .id(1)
                .user(userDto2)
                .category(categoryDto2)
                .memberTier(memberTier)
                .title("title2")
                .content("content2")
                .build();
        List<BoardDto> result = new ArrayList<>();
        result.add(boardDto1);
        result.add(boardDto2);

        //when
        //then
        Assertions.assertAll(
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.inquiryAllBoard(-1, 0)),
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.inquiryAllBoard(5, 4))
        );
    }

}
