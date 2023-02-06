package org.waldreg.controller.board.board.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.waldreg.board.board.management.BoardManager.BoardRequest;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.controller.board.board.request.BoardCreateRequest;
import org.waldreg.controller.board.board.response.ReactionResponse;
import org.waldreg.controller.board.response.author.Author;
import org.waldreg.controller.board.board.response.BoardResponse;

public class ControllerBoardMapper{

    public BoardRequest BoardCreateRequestToBoardRequest(BoardCreateRequest boardCreateRequest){
        return BoardRequest.builder()
                .title(boardCreateRequest.getTitle())
                .content(boardCreateRequest.getContent())
                .categoryId(boardCreateRequest.getCategoryId())
                .build();
    }

    public BoardResponse BoardDtoToBoardResponse(BoardDto boardDto){
        return BoardResponse.builder()
                .id(boardDto.getId())
                //.category(boardDto.getCategoryName())
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .author(userDtoToAuthor(boardDto.getUserDto()))
                .createdAt(boardDto.getCreatedAt())
                .lastModifiedAt(boardDto.getLastModifiedAt())
                .images(StringListToArray(boardDto.getImageUrls()))
                .files(StringListToArray(boardDto.getFileUrls()))
                .existFile(isExistFile(boardDto.getFileUrls()))
                .reaction(reactionDtoToReaction(boardDto.getReactions()))
                .build();
    }

    private Author userDtoToAuthor(UserDto userDto){
        return Author.builder()
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .build();
    }

    private String[] StringListToArray(List<String> list){
        return list.toArray(new String[list.size()]);
    }

    private String isExistFile(List<String> fileUrls){
        if (fileUrls.isEmpty()){
            return "false";
        }
        return "true";
    }

    private ReactionResponse reactionDtoToReaction(ReactionDto reactionDto){
        Map<BoardServiceReactionType, List<UserDto>> reactionMap = reactionDto.getReactionMap();
        List<String> users = new ArrayList<>();
        for (Map.Entry<BoardServiceReactionType, List<UserDto>> typeList : reactionMap.entrySet()){
            List<UserDto> userDtoList = typeList.getValue();
            for (UserDto user : userDtoList){
                users.add(user.getUserId());
            }
        }
        return ReactionResponse.builder()
                .good(reactionMap.get(BoardServiceReactionType.GOOD).size())
                .good(reactionMap.get(BoardServiceReactionType.BAD).size())
                .good(reactionMap.get(BoardServiceReactionType.CHECK).size())
                .good(reactionMap.get(BoardServiceReactionType.HEART).size())
                .good(reactionMap.get(BoardServiceReactionType.SMILE).size())
                .good(reactionMap.get(BoardServiceReactionType.SAD).size())
                .users(users.toArray(new String[users.size()]))
                .build();
    }


}
