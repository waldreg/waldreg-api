package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.CommentDto;

public interface BoardRepository{

    void addComment(CommentDto commentDto);

    boolean isExistBoard(int id);

}
