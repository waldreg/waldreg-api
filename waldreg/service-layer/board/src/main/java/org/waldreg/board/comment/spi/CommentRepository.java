package org.waldreg.board.comment.spi;

import java.util.List;
import org.waldreg.board.dto.CommentDto;

public interface CommentRepository{

    CommentDto createComment(CommentDto commentDto);

    int getCommentMaxIdxByBoardId(int boardId);

    List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx);

    CommentDto inquiryCommentById(int commentId);

    void modifyComment(CommentDto commentDto);

    boolean isExistComment(int commentId);

    void deleteComment(int id);

}
