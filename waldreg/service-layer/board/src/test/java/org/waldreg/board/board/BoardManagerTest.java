package org.waldreg.board.board;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.CategoryDoesNotExistException;
import org.waldreg.board.exception.InvalidRangeException;
import org.waldreg.board.board.file.FileInfoGettable;
import org.waldreg.board.board.management.BoardManager;
import org.waldreg.board.board.management.BoardManager.BoardRequest;
import org.waldreg.board.board.management.DefaultBoardManager;
import org.waldreg.board.board.spi.BoardRepository;
import org.waldreg.board.board.spi.BoardInCategoryRepository;
import org.waldreg.board.board.spi.UserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.util.token.DecryptedTokenContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultBoardManager.class, DecryptedTokenContext.class})
public class BoardManagerTest{

    @Autowired
    private BoardManager boardManager;
    @Autowired
    private DecryptedTokenContext decryptedTokenContext;
    @MockBean
    private BoardRepository boardRepository;
    @MockBean
    private BoardInCategoryRepository boardInCategoryRepository;
    @MockBean
    private UserRepository userRepository;
    @MockBean
    private FileInfoGettable fileInfoGettable;

    @BeforeEach
    @AfterEach
    public void INIT_USER_TOKEN(){
        decryptedTokenContext.resolve();
        decryptedTokenContext.hold(1);
    }

    @Test
    @DisplayName("보드 생성 성공 테스트")
    public void CREATE_BOARD_SUCCESS_TEST(){

        //given
        String title = "title";
        String content = "content";

        int id = 1;
        int categoryId = 1;
        ExecutorService executor
                = Executors.newSingleThreadExecutor();

        Future<String> fileName = executor.submit(()-> "asf");
        List<Future<String>> fileNameList = new ArrayList<>();
        fileNameList.add(fileName);

        Mockito.when(boardInCategoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        Mockito.when(fileInfoGettable.getSavedFileNameList()).thenReturn(fileNameList);

        BoardRequest boardRequest = BoardRequest.builder()
                .authorId(id)
                .title(title)
                .content(content)
                .categoryId(categoryId)
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

        UserDto userDto = UserDto.builder()
                .id(id)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .id(1)
                .userDto(userDto)
                .categoryId(categoryId)
                .title(title)
                .content(content)
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("categoryName")
                .id(categoryId)
                .build();

        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.inquiryBoardById(Mockito.anyInt())).thenReturn(boardDto);
        Mockito.when(boardInCategoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);

        //when
        BoardDto result = boardManager.inquiryBoardById(1);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto.getId(), result.getId()),
                () -> Assertions.assertEquals(boardDto.getTitle(), result.getTitle()),
                () -> Assertions.assertEquals(boardDto.getContent(), result.getContent()),
                () -> Assertions.assertEquals(boardDto.getCategoryId(), result.getCategoryId()),
                () -> Assertions.assertEquals(boardDto.getUserDto(), result.getUserDto())
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
        UserDto userDto = UserDto.builder()
                .id(id)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .id(1)
                .userDto(userDto)
                .categoryId(categoryId)
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

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(id2)
                .build();
        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId1)
                .title(title)
                .content(content)
                .build();
        BoardDto boardDto2 = BoardDto.builder()
                .id(1)
                .userDto(userDto2)
                .categoryId(categoryId2)
                .title("title2")
                .content("content2")
                .build();
        List<BoardDto> result = new ArrayList<>();
        result.add(boardDto1);
        result.add(boardDto2);

        //when
        Mockito.when(boardRepository.getBoardMaxIdx()).thenReturn(2);
        Mockito.when(boardRepository.inquiryAllBoard(Mockito.anyInt(), Mockito.anyInt())).thenReturn(result);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto1.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(boardDto1.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardDto1.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertEquals(boardDto1.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardDto2.getId(), result.get(1).getId()),
                () -> Assertions.assertEquals(boardDto2.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardDto2.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertEquals(boardDto2.getContent(), result.get(1).getContent()));

    }


    @Test
    @DisplayName("전체 게시글 조회 실패 - 올바르지 않은 범위")
    public void INQUIRY_ALL_BOARD_INVALID_RANGE_TEST(){

        //given
        //when
        //then
        Assertions.assertAll(
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.inquiryAllBoard(-1, 0)),
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.inquiryAllBoard(5, 4))
        );
    }

    @Test
    @DisplayName("카테고리별 전체 게시글 조회 성공")
    public void INQUIRY_ALL_BOARD_BY_CATEGORY_SUCCESS_TEST(){

        //given
        String title = "title";
        String content = "content";

        int id1 = 1;
        int categoryId1 = 1;

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .build();

        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId1)
                .title(title)
                .content(content)
                .build();
        BoardDto boardDto3 = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId1)
                .title("title3")
                .content("content3")
                .build();
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("categoryName")
                .id(categoryId1)
                .build();

        List<BoardDto> result2 = new ArrayList<>();
        result2.add(boardDto1);
        result2.add(boardDto3);

        //when
        Mockito.when(boardRepository.getBoardMaxIdxByCategory(Mockito.anyInt())).thenReturn(2);
        Mockito.when(boardInCategoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.inquiryAllBoardByCategory(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(result2);
        Mockito.when(boardInCategoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);
        List<BoardDto> result = boardManager.inquiryAllBoardByCategory(1, 1, 2);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto1.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(boardDto1.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardDto1.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertEquals(boardDto1.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardDto3.getId(), result.get(1).getId()),
                () -> Assertions.assertEquals(boardDto3.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardDto3.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertEquals(boardDto3.getContent(), result.get(1).getContent()));
    }

    @Test
    @DisplayName("카테고리별 전체 게시글 조회 실패 - 조회 하려는 카테고리가 없는 경우")
    public void INQUIRY_ALL_BOARD_BY_CATEGORY_FAIL_TEST(){

        //given

        //when
        Mockito.when(boardInCategoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(false);
        //then
        Assertions.assertThrows(CategoryDoesNotExistException.class, () -> boardManager.inquiryAllBoardByCategory(2, 1, 2));
    }

    @Test
    @DisplayName("카테고리별 전체 게시글 조회 실패 - 올바르지 않은 범위")
    public void INQUIRY_ALL_BOARD_CATEGORY_FAIL_INVALID_RANGE_TEST(){

        //given
        int categoryId = 3;
        //when
        Mockito.when(boardInCategoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        //then
        Assertions.assertAll(
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.inquiryAllBoardByCategory(categoryId, -1, 0)),
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.inquiryAllBoardByCategory(categoryId, 5, 4))
        );
    }


    @Test
    @DisplayName("제목으로 게시글 검색 성공")
    public void SEARCH_BOARD_BY_TITLE_TEST(){
        //given
        String title = "title";
        String content = "content";
        int id1 = 1;
        int id2 = 2;
        int categoryId1 = 1;
        int categoryId2 = 2;

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(id2)
                .build();

        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId1)
                .title(title)
                .content(content)
                .build();
        BoardDto boardDto2 = BoardDto.builder()
                .id(1)
                .userDto(userDto2)
                .categoryId(categoryId2)
                .title("title2")
                .content("content2")
                .build();
        List<BoardDto> resultRepo = new ArrayList<>();
        resultRepo.add(boardDto1);
        resultRepo.add(boardDto2);

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("categoryName")
                .id(categoryId1)
                .build();
        //when
        String searchKeyword = "title";

        Mockito.when(boardRepository.getBoardMaxIdxByTitle(Mockito.anyString())).thenReturn(2);
        Mockito.when(boardRepository.searchByTitle(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(resultRepo);
        Mockito.when(boardInCategoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);
        List<BoardDto> result = boardManager.searchBoardByTitle(searchKeyword, 1, 2);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto1.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(boardDto1.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardDto1.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertEquals(boardDto1.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardDto2.getId(), result.get(1).getId()),
                () -> Assertions.assertEquals(boardDto2.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardDto2.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertEquals(boardDto2.getContent(), result.get(1).getContent())
        );

    }

    @Test
    @DisplayName("제목으로 검색 실패 - 올바르지 않은 범위")
    public void SEARCH_BOARD_BY_TITLE_INVALID_RANGE_TEST(){
        //given
        String searchKeyword = "title";
        //when
        //then
        Assertions.assertAll(
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.searchBoardByTitle(searchKeyword, -1, 0)),
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.searchBoardByTitle(searchKeyword, 5, 4))
        );
    }

    @Test
    @DisplayName("내용으로 게시글 검색 성공")
    public void SEARCH_BOARD_BY_CONTENT_TEST(){
        //given
        String title = "title";
        String content = "content";
        int id1 = 1;
        int id2 = 2;
        int categoryId1 = 1;
        int categoryId2 = 2;

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(id2)
                .build();

        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId1)
                .title(title)
                .content(content)
                .build();
        BoardDto boardDto2 = BoardDto.builder()
                .id(1)
                .userDto(userDto2)
                .categoryId(categoryId2)
                .title("title2")
                .content("content2")
                .build();
        List<BoardDto> resultRepo = new ArrayList<>();
        resultRepo.add(boardDto1);
        resultRepo.add(boardDto2);
        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("categoryName")
                .id(categoryId1)
                .build();

        //when
        String searchKeyword = "content";
        Mockito.when(boardRepository.getBoardMaxIdxByContent(Mockito.anyString())).thenReturn(2);
        Mockito.when(boardRepository.searchByContent(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(resultRepo);
        Mockito.when(boardInCategoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);
        List<BoardDto> result = boardManager.searchBoardByContent(searchKeyword, 1, 2);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto1.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(boardDto1.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardDto1.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertEquals(boardDto1.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardDto2.getId(), result.get(1).getId()),
                () -> Assertions.assertEquals(boardDto2.getTitle(), result.get(1).getTitle()),
                () -> Assertions.assertEquals(boardDto2.getCategoryId(), result.get(1).getCategoryId()),
                () -> Assertions.assertEquals(boardDto2.getContent(), result.get(1).getContent())
        );

    }

    @Test
    @DisplayName("내용으로 검색 실패 - 올바르지 않은 범위")
    public void SEARCH_BOARD_BY_CONTENT_INVALID_RANGE_TEST(){
        //given
        String searchKeyword = "content";
        //when
        //then
        Assertions.assertAll(
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.searchBoardByContent(searchKeyword, -1, 0)),
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.searchBoardByContent(searchKeyword, 5, 4))
        );
    }

    @Test
    @DisplayName("유저아이디으로 게시글 검색 성공")
    public void SEARCH_BOARD_BY_AUTHOR_USER_ID_TEST(){
        //given
        String title = "title";
        String content = "content";
        int id1 = 1;
        int categoryId1 = 1;

        UserDto userDto1 = UserDto.builder()
                .id(id1)
                .userId("Fixtar")
                .build();

        BoardDto boardDto1 = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId1)
                .title(title)
                .content(content)
                .build();

        CategoryDto categoryDto = CategoryDto.builder()
                .categoryName("categoryName")
                .id(categoryId1)
                .build();

        //when
        String searchKeyword = "Fixtar";
        Mockito.when(boardRepository.getBoardMaxIdxByAuthorUserId(Mockito.anyString())).thenReturn(1);
        Mockito.when(boardRepository.searchByAuthorUserId(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(List.of(boardDto1));
        Mockito.when(boardInCategoryRepository.inquiryCategoryById(Mockito.anyInt())).thenReturn(categoryDto);
        List<BoardDto> result = boardManager.searchBoardByAuthorUserId(searchKeyword, 1, 2);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto1.getId(), result.get(0).getId()),
                () -> Assertions.assertEquals(boardDto1.getTitle(), result.get(0).getTitle()),
                () -> Assertions.assertEquals(boardDto1.getCategoryId(), result.get(0).getCategoryId()),
                () -> Assertions.assertEquals(boardDto1.getContent(), result.get(0).getContent()),
                () -> Assertions.assertEquals(boardDto1.getUserDto().getUserId(), result.get(0).getUserDto().getUserId())

        );
    }

    @Test
    @DisplayName("유저 아이디로 검색 실패 - 올바르지 않은 범위")
    public void SEARCH_BOARD_BY_AUTHOR_INVALID_RANGE_TEST(){
        //given
        String searchKeyword = "Fixtar";
        //when
        //then
        Assertions.assertAll(
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.searchBoardByAuthorUserId(searchKeyword, -1, 0)),
                () -> Assertions.assertThrows(InvalidRangeException.class, () -> boardManager.searchBoardByAuthorUserId(searchKeyword, 5, 4))
        );
    }

    @Test
    @DisplayName("게시글 수정 성공")
    public void MODIFY_BOARD_SUCCESS_TEST(){
        //given
        String title = "title";
        String content = "content";

        int id = 1;
        int categoryId = 1;

        UserDto userDto1 = UserDto.builder()
                .id(id)
                .build();
        BoardDto boardDto = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId)
                .title(title)
                .content(content)
                .fileUrls(new ArrayList<>())
                .imageUrls(new ArrayList<>())
                .build();

        String modifyTitle = "modifyTitle";
        String modifyContent = "modifyContent";
        List<String> deleteFileNameList = new ArrayList<>();
        deleteFileNameList.add("delete");
        BoardRequest modifiedBoardRequestDto = BoardRequest.builder()
                .id(boardDto.getId())
                .authorId(boardDto.getUserDto().getId())
                .title(modifyTitle)
                .content(modifyContent)
                .categoryId(boardDto.getCategoryId())
                .deleteFileNameList(deleteFileNameList)
                .build();

        ExecutorService executor
                = Executors.newSingleThreadExecutor();

        Future<String> fileName = executor.submit(()-> "asf");
        List<Future<String>> fileNameList = new ArrayList<>();
        fileNameList.add(fileName);

        Future<Boolean> isFileDeleted = executor.submit(()->true);

        Mockito.when(boardInCategoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.inquiryBoardById(Mockito.anyInt())).thenReturn(boardDto);
        Mockito.when(fileInfoGettable.getSavedFileNameList()).thenReturn(fileNameList);
        Mockito.when(fileInfoGettable.isFileDeleteSuccess()).thenReturn(isFileDeleted);
        //when
        //then
        Assertions.assertDoesNotThrow(() -> boardManager.modifyBoard(modifiedBoardRequestDto));

    }


    @Test
    @DisplayName("게시글 수정 실패 - 카테고리가 없을 때")
    public void MODIFY_BOARD_Fail_CATEGORY_NOT_EXIST_TEST(){
        //given
        String title = "title";
        String content = "content";

        int id = 1;
        int categoryId = 1;

        UserDto userDto1 = UserDto.builder()
                .id(id)
                .build();

        BoardDto boardDto = BoardDto.builder()
                .id(1)
                .userDto(userDto1)
                .categoryId(categoryId)
                .title(title)
                .content(content)
                .fileUrls(new ArrayList<>())
                .imageUrls(new ArrayList<>())
                .build();

        String modifyTitle = "modifyTitle";
        String modifyContent = "modifyContent";
        List<String> deleteFileNameList = new ArrayList<>();
        deleteFileNameList.add("delete");
        BoardRequest modifiedBoardRequestDto = BoardRequest.builder()
                .id(boardDto.getId())
                .authorId(boardDto.getUserDto().getId())
                .title(modifyTitle)
                .content(modifyContent)
                .categoryId(boardDto.getCategoryId())
                .deleteFileNameList(deleteFileNameList)
                .build();

        Mockito.when(boardInCategoryRepository.isExistCategory(Mockito.anyInt())).thenReturn(false);
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.inquiryBoardById(Mockito.anyInt())).thenReturn(boardDto);
        //when
        //then
        Assertions.assertThrows(CategoryDoesNotExistException.class, () -> boardManager.modifyBoard(modifiedBoardRequestDto));

    }

    @Test
    @DisplayName("게시글 삭제 성공")
    public void DELETE_BOARD_SUCCESS_TEST(){
        //given
        int boardId = 1;
        //when
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        //then
        Assertions.assertDoesNotThrow(() -> boardManager.deleteBoard(boardId));
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 없는 게시글")
    public void DELETE_BOARD_FAIL_TEST(){
        //given
        int boardId = 1;
        //when
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(false);
        //then
        Assertions.assertThrows(BoardDoesNotExistException.class, () -> boardManager.deleteBoard(boardId));
    }

}
