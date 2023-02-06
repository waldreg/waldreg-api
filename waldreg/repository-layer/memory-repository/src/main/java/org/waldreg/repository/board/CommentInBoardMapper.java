package org.waldreg.repository.board;

import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.comment.Comment;

public interface CommentInBoardMapper{

    public CommentDto commentDomainToCommentDto(Comment comment);

    public Comment commentDtoToCommentDomain(CommentDto commentDto);

}
