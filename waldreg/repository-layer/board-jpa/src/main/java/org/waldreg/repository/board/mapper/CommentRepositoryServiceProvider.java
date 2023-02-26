package org.waldreg.repository.board.mapper;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.CommentDto;

@Repository
public class CommentRepositoryServiceProvider implements CommentRepository{

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
