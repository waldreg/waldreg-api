package org.waldreg.repository.board;

import java.util.ArrayList;
import java.util.List;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.comment.Comment;

public interface CommentInBoardMapper{

    List<Comment> commentDtoListToCommentDomainList(List<CommentDto> commentDtoList);

    List<CommentDto> commentDomainListToCommentDtoList(List<Comment> commentList);

    CommentDto commentDomainToCommentDto(Comment comment);

    Comment commentDtoToCommentDomain(CommentDto commentDto);

}
