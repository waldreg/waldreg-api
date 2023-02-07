package org.waldreg.repository.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.reaction.Reaction;
import org.waldreg.domain.board.reaction.ReactionType;
import org.waldreg.domain.user.User;
import org.waldreg.repository.category.BoardInCategoryMapper;

@Service
public class BoardMapper implements BoardInCategoryMapper{

    private final CommentInBoardMapper commentInBoardMapper;
    private UserInBoardMapper userInBoardMapper;

    @Autowired
    public BoardMapper(CommentInBoardMapper commentInBoardMapper, UserInBoardMapper userInBoardMapper){
        this.commentInBoardMapper = commentInBoardMapper;
        this.userInBoardMapper = userInBoardMapper;
    }

    @Override
    public List<Board> boardDtoListToBoardDomainList(List<BoardDto> boardDtoList){
        List<Board> boardList = new ArrayList<>();
        for (BoardDto boardDto : boardDtoList){
            boardList.add(boardDtoToBoardDomain(boardDto));
        }
        return boardList;
    }

    @Override
    public Board boardDtoToBoardDomain(BoardDto boardDto){
        Board.Builder builder = Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .categoryId(boardDto.getCategoryId())
                .user(userDtoToUserDomain(boardDto.getUserDto()))
                .filePathList(boardDto.getFileUrls())
                .imagePathList(boardDto.getImageUrls());
        if (isCreateBoard(boardDto)){
            return builder.build();
        }
        return boardDtoToBoardDomainIfNotCreateBoard(boardDto, builder);
    }

    boolean isCreateBoard(BoardDto boardDto){
        return boardDto.getCreatedAt() == null;
    }

    public Board boardDtoToBoardDomainIfNotCreateBoard(BoardDto boardDto, Board.Builder builder){
        builder = builder
                .id(boardDto.getId())
                .reactions(reactionDtoToReactionDomain(boardDto.getReactions().getReactionMap()))
                .createdAt(boardDto.getCreatedAt())
                .lastModifiedAt(boardDto.getLastModifiedAt())
                .views(boardDto.getViews());
        if (isCommentListNotEmpty(boardDto.getCommentList())){
            builder = builder.commentList(commentInBoardMapper.commentDtoListToCommentDomainList(boardDto.getCommentList()));
        }
        return builder.build();
    }

    private boolean isCommentListNotEmpty(List<CommentDto> commentList){
        return commentList != null;
    }

    public Reaction reactionDtoToReactionDomain(Map<BoardServiceReactionType, List<UserDto>> reactionMap){
        Map<ReactionType, List<User>> reactionTypeListMap = new HashMap<>();
        for (Map.Entry<BoardServiceReactionType, List<UserDto>> reactionEntry : reactionMap.entrySet()){
            reactionTypeListMap.put(ReactionTypeToBoardServiceReactionType(reactionEntry.getKey()), userDtoListToUserDomainList((reactionEntry.getValue())));
        }
        return Reaction.builder()
                .reactionMap(reactionTypeListMap)
                .build();
    }

    private ReactionType ReactionTypeToBoardServiceReactionType(BoardServiceReactionType boardServiceReactionType){
        return ReactionType.valueOf(boardServiceReactionType.name());
    }

    private List<User> userDtoListToUserDomainList(List<UserDto> userDtoList){
        List<User> userList = new ArrayList<>();
        for (UserDto userDto : userDtoList){
            userList.add(userDtoToUserDomain(userDto));
        }
        return userList;
    }



    public User userDtoToUserDomain(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .build();
    }

    @Override
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
                .categoryId(board.getCategoryId())
                .content(board.getContent())
                .userDto(userInBoardMapper.userDomainToUserDto(board.getUser()))
                .createdAt(board.getCreatedAt())
                .lastModifiedAt(board.getLastModifiedAt())
                .fileUrls(board.getFilePathList())
                .imageUrls(board.getImagePathList())
                .reactions(reactionDomainToReactionDto(board.getReactions().getReactionMap()))
                .commentList(commentInBoardMapper.commentDomainListToCommentDtoList(board.getCommentList()))
                .views(board.getViews())
                .build();
    }

    private ReactionDto reactionDomainToReactionDto(Map<ReactionType, List<User>> reactionMap){
        Map<BoardServiceReactionType, List<UserDto>> reactionTypeListMap = new HashMap<>();
        for (Map.Entry<ReactionType, List<User>> reactionEntry : reactionMap.entrySet()){
            reactionTypeListMap.put(boardServiceReactionTypeToReactionType(reactionEntry.getKey()), userDomainListToUserDtoList(reactionEntry.getValue()));
        }
        return ReactionDto.builder()
                .reactionMap(reactionTypeListMap)
                .build();
    }

    private BoardServiceReactionType boardServiceReactionTypeToReactionType(ReactionType reactionType){
        return BoardServiceReactionType.valueOf(reactionType.name());
    }

    private List<UserDto> userDomainListToUserDtoList(List<User> userList){
        List<UserDto> userDtoList = new ArrayList<>();
        for (User user : userList){
            userDtoList.add(userInBoardMapper.userDomainToUserDto(user));
        }
        return userDtoList;
    }

}
