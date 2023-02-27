package org.waldreg.repository.board.mapper;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.board.comment.Comment;

@Component
public class CommentRepositoryMapper{

    public Comment commentDtoToCommentDomain(CommentDto commentDto){
        return Comment.builder()
                .content(commentDto.getContent())
                .build();
    }

    public List<CommentDto> commentDomainListToCommentDtoList(List<Comment> commentList){
        List<CommentDto> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList){
            commentDtoList.add(commentDomainToCommentDto(comment));
        }
        return commentDtoList;
    }

    public CommentDto commentDomainToCommentDto(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .boardId(comment.getBoard().getId())
                .userDto(UserDto.builder()
                                 .id(comment.getUser().getId())
                                 .userId(comment.getUser().getUserId())
                                 .name(comment.getUser().getName())
                                 .build())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .content(comment.getContent())
                .build();
    }

}
