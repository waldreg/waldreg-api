package org.waldreg.board.comment.spi;

public interface BoardRepository{

    void addComment(int boardId, int commentId);

    void deleteComment(int boardId, int commentId);

}
