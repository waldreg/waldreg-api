package org.waldreg.board.comment.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.CommentDoesNotExistException;
import org.waldreg.board.exception.ContentLengthOverThousandException;
import org.waldreg.board.exception.InvalidRangeException;
import org.waldreg.board.comment.spi.CommentInBoardRepository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.CommentDto;

public class DefaultCommentManager implements CommentManager{

    private final int perPage = 20;
    private CommentRepository commentRepository;

    private CommentInBoardRepository commentInBoardRepository;

    @Autowired
    public void DefaultBoardManager(CommentRepository commentRepository, CommentInBoardRepository commentInBoardRepository){
        this.commentRepository = commentRepository;
        this.commentInBoardRepository = commentInBoardRepository;
    }


    @Override
    public void createComment(CommentDto commentDto){
        throwIfBoardDoesNotExist(commentDto.getBoardId());
        throwIfContentOverFlowThousand(commentDto.getContent());
        CommentDto storedCommentDto = commentRepository.createComment(commentDto);
        commentInBoardRepository.addComment(storedCommentDto);
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!commentInBoardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException(boardId);
        }
    }

    private void throwIfContentOverFlowThousand(String content){
        if (content.length() > 1000){
            throw new ContentLengthOverThousandException();
        }
    }

    @Override
    public List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx){
        throwIfBoardDoesNotExist(boardId);
        throwIfInvalidRangeDetected(startIdx, endIdx);
        int maxIdx = commentRepository.getCommentMaxIdxByBoardId(boardId);
        endIdx = adjustEndIdx(startIdx, endIdx, maxIdx);
        return commentRepository.inquiryAllCommentByBoardId(boardId, startIdx, endIdx);
    }

    private void throwIfInvalidRangeDetected(int startIdx, int endIdx){
        if (startIdx > endIdx || startIdx < 0){
            throw new InvalidRangeException(startIdx, endIdx);
        }
    }

    private int adjustEndIdx(int startIdx, int endIdx, int maxIdx){
        if (maxIdx < endIdx){
            endIdx = maxIdx;
        }
        if (endIdx - startIdx + 1 > perPage){
            return startIdx + perPage - 1;
        }
        return endIdx;
    }

    @Override
    public void modifyComment(CommentDto commentDto){
        throwIfBoardDoesNotExist(commentDto.getBoardId());
        throwIfCommentDoesNotExist(commentDto.getId());
        throwIfContentOverFlowThousand(commentDto.getContent());
        commentRepository.modifyComment(commentDto);
    }

    private void throwIfCommentDoesNotExist(int commentId){
        if (!commentRepository.isExistComment(commentId)){
            throw new CommentDoesNotExistException(commentId);
        }
    }

    @Override
    public void deleteComment(int commentId){
        throwIfCommentDoesNotExist(commentId);
        commentRepository.deleteComment(commentId);
    }

}