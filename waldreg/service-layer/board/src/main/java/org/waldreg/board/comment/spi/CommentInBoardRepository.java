package org.waldreg.board.comment.spi;

import org.waldreg.board.dto.CommentDto;

public interface CommentInBoardRepository{

    void addCommentInBoardCommentList(CommentDto commentDto);

    boolean isExistBoard(int id);

}
