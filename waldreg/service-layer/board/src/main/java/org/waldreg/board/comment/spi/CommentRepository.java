package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.CommentDto;

public interface CommentRepository{

    void createComment(CommentDto commentDto);

    void modifyComment(CommentDto commentDto);

    void deleteComment(int id);

}
