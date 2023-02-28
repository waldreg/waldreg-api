package org.waldreg.repository.board.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.user.User;

@Component
public class BoardRepositoryMapper{

    private final CommentRepositoryMapper commentRepositoryMapper;
    private final ReactionRepositoryMapper reactionRepositoryMapper;

    public BoardRepositoryMapper(CommentRepositoryMapper commentRepositoryMapper, ReactionRepositoryMapper reactionRepositoryMapper){
        this.commentRepositoryMapper = commentRepositoryMapper;
        this.reactionRepositoryMapper = reactionRepositoryMapper;
    }

    public Board boardDtoToBoardDomain(BoardDto boardDto){
        return Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .filePathList(boardDto.getFileUrls())
                .imagePathList(boardDto.getImageUrls())
                .build();
    }

    public List<BoardDto> boardDomainListToBoardDtoList(List<Board> boardList){
        List<BoardDto> boardDtoList = new ArrayList<>();
        for (Board board : boardList){
            boardDtoList.add(boardDomainToBoardDto(board));
        }
        return boardDtoList;
    }
    public BoardDto boardDomainToBoardDto(Board board){
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .categoryId(board.getCategory().getId())
                .content(board.getContent())
                .userDto(userToUserDto(board.getUser()))
                .createdAt(board.getCreatedAt())
                .lastModifiedAt(board.getLastModifiedAt())
                .fileUrls(board.getFilePathList())
                .imageUrls(board.getImagePathList())
                .reactions(reactionRepositoryMapper.reactionDomainListToReactionDto(board.getReactionList(),board.getId()))
                .commentList(commentRepositoryMapper.commentDomainListToCommentDtoList(board.getCommentList()))
                .views(board.getViews())
                .build();
    }

    public UserDto userToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .userId(user.getUserId())
                .build();
    }

}
