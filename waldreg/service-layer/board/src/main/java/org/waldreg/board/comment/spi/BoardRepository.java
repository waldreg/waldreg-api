package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.CommentDto;

public interface BoardRepository{

    void addComment(CommentDto commentDto);

    void deleteComment(int boardId, int commentId);

    boolean isExistBoard(int id);

}
