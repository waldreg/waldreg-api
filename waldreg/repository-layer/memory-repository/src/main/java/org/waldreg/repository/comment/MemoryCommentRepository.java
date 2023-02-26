package org.waldreg.repository.comment;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.BoardDto;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.repository.MemoryBoardStorage;
import org.waldreg.repository.MemoryCommentStorage;

@Repository
public class MemoryCommentRepository implements CommentRepository{

    @Override
    public CommentDto createComment(CommentDto commentDto){
        return null;
    }

    @Override
    public int getCommentMaxIdxByBoardId(int boardId){
        return 0;
    }

    @Override
    public List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx){
        return null;
    }

    @Override
    public CommentDto inquiryCommentById(int commentId){
        return null;
    }

    @Override
    public void modifyComment(CommentDto commentDto){

    }

    @Override
    public boolean isExistComment(int commentId){
        return false;
    }

    @Override
    public void deleteComment(int id){

    }

}
