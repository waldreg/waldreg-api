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

    private final MemoryBoardStorage memoryBoardStorage;
    private final MemoryCommentStorage memoryCommentStorage;
    private final CommentMapper commentMapper;

    @Autowired
    public MemoryCommentRepository(MemoryBoardStorage memoryBoardStorage, MemoryCommentStorage memoryCommentStorage, CommentMapper commentMapper){
        this.memoryBoardStorage = memoryBoardStorage;
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
        return memoryCommentStorage.getCommentMaxIdxByBoardId(boardId);
    }

    @Override
    public List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx){
        List<Comment> commentList = memoryCommentStorage.inquiryAllCommentByBoardId(boardId, startIdx - 1, endIdx - 1);
        return commentMapper.commentDomainListToCommentDtoList(commentList);
    }

    @Override
    public void modifyComment(CommentDto commentDto){
        int boardId = commentDto.getBoardId();
        Comment comment = commentMapper.commentDtoToCommentDomain(commentDto);
        memoryCommentStorage.modifyComment(comment);
        updateBoardCommentList(boardId);
    }

    @Override
    public boolean isExistComment(int commentId){
        return memoryCommentStorage.isExistComment(commentId);
    }

    @Override
    public void deleteComment(int id){
        int boardId = memoryCommentStorage.getCommentBoardIdByCommentId(id);
        memoryCommentStorage.deleteComment(id);
        updateBoardCommentList(boardId);
    }

    private void updateBoardCommentList(int boardId){
        int startIdx = 1;
        int maxIdx = getCommentMaxIdxByBoardId(boardId);
        Board board = memoryBoardStorage.inquiryBoardById(boardId);
        List<CommentDto> commentDtoList = inquiryAllCommentByBoardId(boardId, startIdx, maxIdx);
        board.setCommentList(commentMapper.commentDtoListToCommentDomainList(commentDtoList));
        memoryBoardStorage.modifyBoard(board);
    }

}
