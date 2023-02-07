package org.waldreg.repository.board;

import java.util.List;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.comment.Comment;

public interface CommentInBoardMapper{

    List<CommentDto> commentDomainListToCommentDtoList(List<Comment> commentList);

    public CommentDto commentDomainToCommentDto(Comment comment);

    public Comment commentDtoToCommentDomain(CommentDto commentDto);

}
