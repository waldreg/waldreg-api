package org.waldreg.repository.board.mapper;

import java.util.List;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.comment.Comment;

public interface CommentRepositoryMapper{

    List<Comment> commentDtoListToCommentDomainList(List<CommentDto> commentDtoList);
    List<CommentDto> commentDomainListToCommentDtoList(List<Comment> commentList);

    CommentDto commentDomainToCommentDto(Comment comment);

    Comment commentDtoToCommentDomain(CommentDto commentDto);

}
