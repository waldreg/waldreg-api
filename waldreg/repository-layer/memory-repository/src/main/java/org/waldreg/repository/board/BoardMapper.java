package org.waldreg.repository.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.BoardServiceMemberTier;
import org.waldreg.board.dto.BoardServiceReactionType;
import org.waldreg.board.dto.CategoryDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.ReactionDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.board.reaction.ReactionType;
import org.waldreg.domain.category.Category;
import org.waldreg.domain.character.Character;
import org.waldreg.domain.character.Permission;
import org.waldreg.domain.tier.MemberTier;
import org.waldreg.domain.user.User;

@Service
public class BoardMapper{

    public Board boardDtoToBoardDomain(BoardDto boardDto){
        return Board.builder()
                .title(boardDto.getTitle())
                .content(boardDto.getContent())
                .category(categoryDtoToCategoryDomain(boardDto.getCategoryDto()))
                .memberTier(boardServiceMemberTierToMemberTier(boardDto.getMemberTier()))
                .build();
    }

    private Category categoryDtoToCategoryDomain(CategoryDto categoryDto){
        return Category.builder()
                .categoryName(categoryDto.getCategoryName())
                .memberTier(boardServiceMemberTierToMemberTier(categoryDto.getMemberTier()))
                .build();
    }

    private MemberTier boardServiceMemberTierToMemberTier(BoardServiceMemberTier boardServiceMemberTier){
        return MemberTier.valueOf(boardServiceMemberTier.name());
    }

    public BoardDto boardDomainToBoardDto(Board board){
        return BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .categoryDto(categoryDomainToCategoryDto(board.getCategory()))
                .content(board.getContent())
                .userDto(userDomainToUserDto(board.getUser()))
                .createdAt(board.getCreatedAt())
                .lastModifiedAt(board.getLastModifiedAt())
                .boardServiceMemberTier(memberTierToBoardServiceMemberTier(board.getMemberTier()))
                .reactions(reactionDomainToReactionDto(board.getReactions().getReactionMap()))
                .commentList(commentDomainListToCommentDtoList(board.getCommentList()))
                .views(board.getViews())
                .build();
    }

    private CategoryDto categoryDomainToCategoryDto(Category category){
        return CategoryDto.builder()
                .categoryName(category.getCategoryName())
                .memberTier(memberTierToBoardServiceMemberTier(category.getMemberTier()))
                .build();
    }

    private BoardServiceMemberTier memberTierToBoardServiceMemberTier(MemberTier memberTier){
        return BoardServiceMemberTier.valueOf(memberTier.name());
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
            userDtoList.add(userDomainToUserDto(user));
        }
        return userDtoList;
    }

    private List<CommentDto> commentDomainListToCommentDtoList(List<Comment> commentList){
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList){
            commentDtoList.add(CommentDto.builder()
                    .id(comment.getId())
                    .userDto(userDomainToUserDto(comment.getUser()))
                    .createdAt(comment.getCreatedAt())
                    .lastModifiedAt(comment.getLastModifiedAt())
                    .content(comment.getContent())
                    .build());
        }
        return commentDtoList;
    }

    private UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .memberTier(BoardServiceMemberTier.valueOf(findTier(user.getCharacter())))
                .name(user.getName())
                .build();
    }

    private String findTier(Character character){
        List<Permission> permissionList = character.getPermissionList();
        for (Permission permission : permissionList){
            if (isPermissionTierTrue(permission)){
                return permission.getName();
            }
        }
        throw new IllegalStateException();
    }

    private boolean isPermissionTierTrue(Permission permission){
        return permission.getName().contains("TIER") && permission.getStatus().equals("true");
    }

}