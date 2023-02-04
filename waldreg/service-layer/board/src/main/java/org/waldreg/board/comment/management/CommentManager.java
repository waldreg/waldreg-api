package org.waldreg.board.comment.management;

import java.util.List;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;

public interface CommentManager{

    void createComment(CommentDto commentDto);

    List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx);

    void modifyComment(CommentDto commentDto);
}
