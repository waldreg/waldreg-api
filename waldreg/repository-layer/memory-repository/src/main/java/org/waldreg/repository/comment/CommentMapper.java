package org.waldreg.repository.comment;

import org.springframework.stereotype.Service;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.CommentInBoardMapper;

@Service
public class CommentMapper implements CommentInBoardMapper{

    @Override
    public CommentDto commentDomainToCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .boardId(comment.getBoardId())
                .userDto(userDomainToUserDto(comment.getUser()))
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .content(comment.getContent())
                .build();
    }

    public UserDto userDomainToUserDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .name(user.getName())
                .build();
    }

    @Override
    public Comment commentDtoToCommentDomain(CommentDto commentDto){
        Comment.Builder builder = Comment.builder()
                .boardId(commentDto.getBoardId())
                .user(userDtoToUserDomain(commentDto.getUserDto()))
                .content(commentDto.getContent());
        if (!isCommentCreate(commentDto)){
            return builder
                    .id(commentDto.getId())
                    .createdAt(commentDto.getCreatedAt())
                    .lastModifiedAt(commentDto.getLastModifiedAt())
                    .build();
        }
        return builder.build();
    }

    private boolean isCommentCreate(CommentDto commentDto){
        return commentDto.getCreatedAt() == null;
    }

    public User userDtoToUserDomain(UserDto userDto){
        return User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .name(userDto.getName())
                .build();
    }

}
