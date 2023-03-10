package org.waldreg.board.reaction.management;


import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.ReactionRequestDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.ReactionTypeDoesNotExistException;
import org.waldreg.board.reaction.spi.ReactionInBoardRepository;
import org.waldreg.board.reaction.spi.ReactionUserRepository;

@Service
public class DefaultReactionManager implements ReactionManager{

    private final ReactionInBoardRepository reactionInBoardRepository;
    private final ReactionUserRepository reactionUserRepository;

    public DefaultReactionManager(ReactionInBoardRepository reactionInBoardRepository, ReactionUserRepository userRepository){
        this.reactionInBoardRepository = reactionInBoardRepository;
        this.reactionUserRepository = userRepository;
    }

    @Override
    public void reactionRequest(ReactionRequestDto reactionRequestDto){
        throwIfBoardDoesNotExist(reactionRequestDto.getBoardId());
        throwIfReactionTypeDoesNotExist(reactionRequestDto.getReactionType());
        Map<BoardServiceReactionType, List<UserDto>> reactionMap = reactionInBoardRepository.getReactionDto(reactionRequestDto.getBoardId()).getReactionMap();
        BoardServiceReactionType beforeType = findBoardServiceReactionTypeByUserId(reactionMap, reactionRequestDto.getUserId());
        reactionMap = allRequestReaction(reactionMap, beforeType, reactionRequestDto);
        reactionInBoardRepository.storeReactionDto(ReactionDto.builder().boardId(reactionRequestDto.getBoardId()).reactionMap(reactionMap).build());
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!reactionInBoardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException("BOARD-401","Unknown board id : " + boardId);
        }
    }

    private void throwIfReactionTypeDoesNotExist(String reactionType){
        try{
            BoardServiceReactionType.valueOf(reactionType.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new ReactionTypeDoesNotExistException("BOARD-402","Invalid reaction type reactionType: " + reactionType);
        }
    }

    private BoardServiceReactionType findBoardServiceReactionTypeByUserId(Map<BoardServiceReactionType, List<UserDto>> reactionMap, String userId){
        for (Map.Entry<BoardServiceReactionType, List<UserDto>> typeList : reactionMap.entrySet()){
            BoardServiceReactionType reactionType = getReactionType(typeList,userId);
            if(reactionType != null){
                return reactionType;
            }
        }
        return null;
    }

    private BoardServiceReactionType getReactionType(Map.Entry<BoardServiceReactionType, List<UserDto>> typeList, String userId){
        List<UserDto> userDtoList = typeList.getValue();
        for (UserDto userDto : userDtoList){
            if (userDto.getUserId().equals(userId)){
                return typeList.getKey();
            }
        }
        return null;
    }

    private Map<BoardServiceReactionType, List<UserDto>> allRequestReaction(Map<BoardServiceReactionType, List<UserDto>> reactionMap, BoardServiceReactionType beforeType, ReactionRequestDto reactionRequestDto){
        BoardServiceReactionType requestType = BoardServiceReactionType.valueOf(reactionRequestDto.getReactionType());
        String userId = reactionRequestDto.getUserId();
        if (beforeType == requestType){
            cancelReaction(reactionMap, beforeType, userId);
        } else if (beforeType == null){
            addReaction(reactionMap, requestType, userId);
        } else{
            cancelReaction(reactionMap, beforeType, userId);
            addReaction(reactionMap, requestType, userId);
        }
        return reactionMap;
    }

    private void cancelReaction(Map<BoardServiceReactionType, List<UserDto>> reactionMap, BoardServiceReactionType beforeType, String userId){
        List<UserDto> typeUserDtoList = reactionMap.get(beforeType);
        cancelReactionUserList(typeUserDtoList, userId);
        reactionMap.put(beforeType, typeUserDtoList);
    }

    private void cancelReactionUserList(List<UserDto> typeUserDtoList, String userId){
        for (UserDto userDto : typeUserDtoList){
            if (userDto.getUserId().equals(userId)){
                typeUserDtoList.remove(userDto);
                return;
            }
        }
    }

    private void addReaction(Map<BoardServiceReactionType, List<UserDto>> reactionMap, BoardServiceReactionType type, String userId){
        List<UserDto> typeUserDtoList = reactionMap.get(type);
        addReactionUserList(typeUserDtoList, userId);
        reactionMap.put(type, typeUserDtoList);
    }

    private void addReactionUserList(List<UserDto> userDtoList, String userId){
        UserDto userDto = reactionUserRepository.getUserInfoByUserId(userId);
        userDtoList.add(userDto);
    }


}
