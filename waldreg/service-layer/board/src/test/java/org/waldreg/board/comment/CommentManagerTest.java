package org.waldreg.board.comment;

import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.User;
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
import org.waldreg.board.comment.exception.BoardDoesNotExistException;
import org.waldreg.board.comment.exception.ContentLengthOverThousandException;
import org.waldreg.board.comment.exception.InvalidRangeException;
import org.waldreg.board.comment.exception.UserDoesNotExistException;
import org.waldreg.board.comment.management.CommentManager;
import org.waldreg.board.comment.management.DefaultCommentManager;
import org.waldreg.board.comment.spi.BoardRepository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.comment.spi.UserRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.util.token.DecryptedTokenContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultCommentManager.class, DecryptedTokenContext.class})
public class CommentManagerTest{

    @Autowired
    private CommentManager commentManager;

    @Autowired
    private DecryptedTokenContext decryptedTokenContext;

    @MockBean
    private CommentRepository commentRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BoardRepository boardRepository;

    @BeforeEach
    @AfterEach
    public void INIT_USER_TOKEN(){
        decryptedTokenContext.resolve();
        decryptedTokenContext.hold(1);
    }

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    public void CREATE_COMMENT_SUCCESS_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();
        int boardId = 2;
        BoardDto boardDto = BoardDto.builder()
                .id(boardId)
                .userDto(userDto)
                .title("title")
                .content("content")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment")
                .id(3)
                .build();
        //when
        Mockito.when(userRepository.isExistUser(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(commentRepository.createComment(Mockito.any())).thenReturn(commentDto);

        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList.add(commentDto);
        boardDto.setCommentList(commentDtoList);

        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(boardDto.getCommentList().get(0).getId(), commentDto.getId()),
                () -> Assertions.assertEquals(boardDto.getCommentList().get(0).getBoardId(), commentDto.getBoardId()),
                () -> Assertions.assertEquals(boardDto.getCommentList().get(0).getContent(), commentDto.getContent()),
                () -> Assertions.assertEquals(boardDto.getCommentList().get(0).getUserDto().getUserId(), commentDto.getUserDto().getUserId()),
                () -> Assertions.assertEquals(boardDto.getCommentList().get(0).getUserDto().getId(), commentDto.getUserDto().getId())
        );
    }

    @Test
    @DisplayName("댓글 생성 실패 - 없는 유저")
    public void CREATE_COMMENT_DOES_NOT_EXIST_USER_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();
        int boardId = 2;

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment")
                .id(3)
                .build();
        //when
        Mockito.when(userRepository.isExistUser(Mockito.anyInt())).thenReturn(false);
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertThrows(UserDoesNotExistException.class, () -> commentManager.createComment(commentDto));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 없는 게시글")
    public void CREATE_COMMENT_DOES_NOT_EXIST_BOARD_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();
        int boardId = 2;

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment")
                .id(3)
                .build();
        //when
        Mockito.when(userRepository.isExistUser(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(false);

        //then
        Assertions.assertThrows(BoardDoesNotExistException.class, () -> commentManager.createComment(commentDto));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 댓글 길이가 1000을 넘었을 때")
    public void CREATE_COMMENT_OVERFLOW_TEST(){
        //given
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();
        int boardId = 2;

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content(
                        "comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment123comment1231234")
                .id(3)
                .build();
        //when
        Mockito.when(userRepository.isExistUser(Mockito.anyInt())).thenReturn(true);
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertThrows(ContentLengthOverThousandException.class, () -> commentManager.createComment(commentDto));
    }


    @Test
    @DisplayName("댓글 전체조회 성공")
    public void INQUIRY_ALL_COMMENT_SUCCESS_TEST(){
        //given
        int boardId = 1;
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment")
                .id(1)
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment2")
                .id(2)
                .build();
        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList.add(commentDto);
        commentDtoList.add(commentDto2);
        //when
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(commentRepository.inquiryAllCommentByBoardId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(commentDtoList);

        List<CommentDto> result = commentManager.inquiryAllCommentByBoardId(boardId, 1, 2);
        //then
        Assertions.assertAll(
                () -> Assertions.assertEquals(result.get(0).getContent(), commentDto.getContent()),
                () -> Assertions.assertEquals(result.get(0).getId(), commentDto.getId()),
                () -> Assertions.assertEquals(result.get(0).getUserDto(), commentDto.getUserDto()),
                () -> Assertions.assertEquals(result.get(1).getContent(), commentDto2.getContent()),
                () -> Assertions.assertEquals(result.get(1).getId(), commentDto2.getId()),
                () -> Assertions.assertEquals(result.get(1).getUserDto(), commentDto2.getUserDto())
        );
    }

    @Test
    @DisplayName("댓글 전체조회 실패 - 없는 게시글")
    public void INQUIRY_ALL_COMMENT_DOES_NOT_EXIST_BOARD_TEST(){
        //given
        int boardId = 1;
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment")
                .id(1)
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment2")
                .id(2)
                .build();
        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList.add(commentDto);
        commentDtoList.add(commentDto2);
        //when
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(false);
        Mockito.when(commentRepository.inquiryAllCommentByBoardId(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(commentDtoList);

        //then
        Assertions.assertThrows(BoardDoesNotExistException.class, () -> commentManager.inquiryAllCommentByBoardId(boardId, 1, 2));
    }

    @Test
    @DisplayName("댓글 전체조회 실패 - 올바르지 않은 범위")
    public void INQUIRY_ALL_COMMENT_INVALID_RANGE_TEST(){
        //given
        int boardId = 1;
        UserDto userDto = UserDto.builder()
                .id(1)
                .userId("Fixtar")
                .name("1234")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment")
                .id(1)
                .build();
        CommentDto commentDto2 = CommentDto.builder()
                .boardId(boardId)
                .userDto(userDto)
                .content("comment2")
                .id(2)
                .build();
        List<CommentDto> commentDtoList = new ArrayList<>();
        commentDtoList.add(commentDto);
        commentDtoList.add(commentDto2);
        //when
        Mockito.when(boardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);

        //then
        Assertions.assertThrows(InvalidRangeException.class, () -> commentManager.inquiryAllCommentByBoardId(boardId, 2, 1));
    }
}
