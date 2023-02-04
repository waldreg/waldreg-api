package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.CommentDto;

public interface CommentRepository{

    CommentDto createComment(CommentDto commentDto);

    void modifyComment(CommentDto commentDto);

    void deleteComment(int id);

}
