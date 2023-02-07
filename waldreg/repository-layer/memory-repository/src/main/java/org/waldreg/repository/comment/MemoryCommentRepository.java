package org.waldreg.repository.comment;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.repository.MemoryCommentStorage;

@Repository
public class MemoryCommentRepository implements CommentRepository{

    private final MemoryCommentStorage memoryCommentStorage;
    private final CommentMapper commentMapper;

    @Autowired
    public MemoryCommentRepository(MemoryCommentStorage memoryCommentStorage, CommentMapper commentMapper){
        this.memoryCommentStorage = memoryCommentStorage;
        this.commentMapper = commentMapper;
    }

    @Override
    public CommentDto createComment(CommentDto commentDto){
        Comment comment = commentMapper.commentDtoToCommentDomain(commentDto);
        comment = memoryCommentStorage.createComment(comment);
        return commentMapper.commentDomainToCommentDto(comment);
    }

    @Override
    public int getCommentMaxIdxByBoardId(int boardId){
        return 0;
    }

    @Override
    public List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx){
        List<Comment> commentList = memoryCommentStorage.inquiryAllCommentByBoardId(boardId, startIdx-1, endIdx-1);
        return commentMapper.commentDomainListToCommentDtoList(commentList);
    }

    @Override
    public void modifyComment(CommentDto commentDto){
        Comment comment = commentMapper.commentDtoToCommentDomain(commentDto);
        memoryCommentStorage.modifyComment(comment);
    }

    @Override
    public boolean isExistComment(int commentId){
        return false;
    }

    @Override
    public void deleteComment(int id){

    }

}
