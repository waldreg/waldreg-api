package org.waldreg.board.reaction.management;


import java.util.List;
import java.util.Map;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.ReactionRequestDto;
import org.waldreg.board.reaction.exception.BoardDoesNotExistException;
import org.waldreg.board.reaction.exception.ReactionTypeDoesNotExistException;
import org.waldreg.board.reaction.spi.BoardRepository;

public class DefaultReactionManager implements ReactionManager{

    private final BoardRepository boardRepository;

    public DefaultReactionManager(BoardRepository boardRepository){
        this.boardRepository = boardRepository;
    }

    @Override
    public void reactionRequest(ReactionRequestDto reactionRequestDto){
        throwIfBoardDoesNotExist(reactionRequestDto.getBoardId());
        throwIfReactionTypeDoesNotExist(reactionRequestDto.getReactionType());
        ReactionDto reactionDto = boardRepository.getReactionDto(reactionRequestDto.getBoardId());
        BoardServiceReactionType beforeType = findBoardServiceReactionTypeByUserId(reactionDto.getReactionMap(), reactionRequestDto.getUserId());
        Map<BoardServiceReactionType, List<String>> reactionMap = allRequestReaction(reactionDto.getReactionMap(), beforeType, reactionRequestDto);
        reactionDto.setReactionMap(reactionMap);
        boardRepository.storeReactionDto(reactionDto);
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!boardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException(boardId);
        }
    }

    private void throwIfReactionTypeDoesNotExist(String reactionType){
        try{
            BoardServiceReactionType.valueOf(reactionType.toUpperCase());
        } catch (IllegalArgumentException e){
            throw new ReactionTypeDoesNotExistException(reactionType);
        }
    }

    private BoardServiceReactionType findBoardServiceReactionTypeByUserId(Map<BoardServiceReactionType, List<String>> reactionMap, String userId){
        for (Map.Entry<BoardServiceReactionType, List<String>> typeList : reactionMap.entrySet()){
            List<String> storedUserIdList = typeList.getValue();
            if (storedUserIdList.contains(userId)){
                return typeList.getKey();
            }
        }
        return null;
    }

    private Map<BoardServiceReactionType, List<String>> allRequestReaction(Map<BoardServiceReactionType, List<String>> reactionMap, BoardServiceReactionType beforeType, ReactionRequestDto reactionRequestDto){
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

    private void cancelReaction(Map<BoardServiceReactionType, List<String>> reactionMap, BoardServiceReactionType beforeType, String userId){
        List<String> typeUserIdList = reactionMap.get(beforeType);
        cancelReactionUserList(typeUserIdList, userId);
        reactionMap.put(beforeType, typeUserIdList);
    }

    private void cancelReactionUserList(List<String> typeUserIdList, String userId){
        typeUserIdList.remove(userId);
    }

    private void addReaction(Map<BoardServiceReactionType, List<String>> reactionMap, BoardServiceReactionType type, String userId){
        List<String> typeUserIdList = reactionMap.get(type);
        addReactionUserList(typeUserIdList, userId);
        reactionMap.put(type, typeUserIdList);
    }

    private void addReactionUserList(List<String> typeUserIdList, String userId){
        typeUserIdList.add(userId);
    }


}
