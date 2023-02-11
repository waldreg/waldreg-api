package org.waldreg.board.reaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.ReactionRequestDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.ReactionTypeDoesNotExistException;
import org.waldreg.board.reaction.management.DefaultReactionManager;
import org.waldreg.board.reaction.management.ReactionManager;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.board.reaction.spi.ReactionUserRepository;
import org.waldreg.util.token.DecryptedTokenContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DefaultReactionManager.class, DecryptedTokenContext.class})
public class ReactionManagerTest{

    @Autowired
    private ReactionManager reactionManager;

    @Autowired
    private DecryptedTokenContext decryptedTokenContext;

    @MockBean
    private ReactionInBoardRepository reactionInBoardRepository;

    @MockBean
    private ReactionUserRepository reactionUserRepository;

    @Test
    @DisplayName("반응 남기기 요청 성공")
    public void ADD_REACTION_SUCCESS_TEST(){
        //given
        int boardId = 1;
        ReactionRequestDto reactionRequestDto = ReactionRequestDto.builder()
                .reactionType("HEART")
                .userId("Fixtar")
                .boardId(boardId)
                .build();

        Map<BoardServiceReactionType, List<UserDto>> reactionMap = new HashMap<>();
        reactionMap.put(BoardServiceReactionType.HEART, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.BAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SMILE, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.GOOD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.CHECK, new ArrayList<>());

        ReactionDto reactionDto = ReactionDto.builder()
                .reactionMap(reactionMap)
                .build();
        //when
        Mockito.when(reactionInBoardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(reactionInBoardRepository.getReactionDto(Mockito.anyInt())).thenReturn(reactionDto);
        //then
        Assertions.assertDoesNotThrow(() -> reactionManager.reactionRequest(reactionRequestDto));
    }

    @Test
    @DisplayName("반응 남기기 요청 실패 - 없는 게시글")
    public void ADD_REACTION_BOARD_DOES_NOT_EXIST_TEST(){
        //given
        int boardId = 1;
        ReactionRequestDto reactionRequestDto = ReactionRequestDto.builder()
                .reactionType("HEART")
                .userId("Fixtar")
                .boardId(boardId)
                .build();

        Map<BoardServiceReactionType, List<UserDto>> reactionMap = new HashMap<>();
        reactionMap.put(BoardServiceReactionType.HEART, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.BAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SMILE, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.GOOD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.CHECK, new ArrayList<>());

        ReactionDto reactionDto = ReactionDto.builder()
                .reactionMap(reactionMap)
                .build();
        //when
        Mockito.when(reactionInBoardRepository.isExistBoard(Mockito.anyInt())).thenReturn(false);
        Mockito.when(reactionInBoardRepository.getReactionDto(Mockito.anyInt())).thenReturn(reactionDto);
        //then
        Assertions.assertThrows(BoardDoesNotExistException.class, () -> reactionManager.reactionRequest(reactionRequestDto));
    }

    @Test
    @DisplayName("반응 남기기 요청 실패 - 없는 타입의 반응")
    public void ADD_REACTION_UNKNOWN_TYPE_TEST(){
        //given
        int boardId = 1;
        ReactionRequestDto reactionRequestDto = ReactionRequestDto.builder()
                .reactionType("fadf")
                .userId("Fixtar")
                .boardId(boardId)
                .build();

        Map<BoardServiceReactionType, List<UserDto>> reactionMap = new HashMap<>();
        reactionMap.put(BoardServiceReactionType.HEART, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.BAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SAD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.SMILE, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.GOOD, new ArrayList<>());
        reactionMap.put(BoardServiceReactionType.CHECK, new ArrayList<>());

        ReactionDto reactionDto = ReactionDto.builder()
                .reactionMap(reactionMap)
                .build();
        //when
        Mockito.when(reactionInBoardRepository.isExistBoard(Mockito.anyInt())).thenReturn(true);
        Mockito.when(reactionInBoardRepository.getReactionDto(Mockito.anyInt())).thenReturn(reactionDto);
        //then
        Assertions.assertThrows(ReactionTypeDoesNotExistException.class, () -> reactionManager.reactionRequest(reactionRequestDto));
    }

}
