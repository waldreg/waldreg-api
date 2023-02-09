package org.waldreg.board.comment.management;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waldreg.board.comment.spi.CommentUserRepository;
import org.waldreg.board.dto.UserDto;
import org.waldreg.board.exception.BoardDoesNotExistException;
import org.waldreg.board.exception.CommentDoesNotExistException;
import org.waldreg.board.exception.ContentOverFlowException;
import org.waldreg.board.exception.InvalidRangeException;
import org.waldreg.board.comment.spi.CommentInBoardRepository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.util.token.DecryptedTokenContextGetter;

@Service
public class DefaultCommentManager implements CommentManager{

    private CommentRepository commentRepository;
    private CommentUserRepository commentUserRepository;
    private CommentInBoardRepository commentInBoardRepository;
    private DecryptedTokenContextGetter decryptedTokenContextGetter;

    @Autowired
    public DefaultCommentManager(CommentRepository commentRepository, CommentUserRepository commentUserRepository, CommentInBoardRepository commentInBoardRepository, DecryptedTokenContextGetter decryptedTokenContextGetter){
        this.commentRepository = commentRepository;
        this.commentUserRepository = commentUserRepository;
        this.commentInBoardRepository = commentInBoardRepository;
        this.decryptedTokenContextGetter = decryptedTokenContextGetter;
    }

    @Override
    public void createComment(CommentDto commentDto){
        throwIfBoardDoesNotExist(commentDto.getBoardId());
        throwIfContentOverFlowThousand(commentDto.getContent());
        int id = decryptedTokenContextGetter.get();
        UserDto userDto = commentUserRepository.getUserInfo(id);
        commentDto.setUserDto(userDto);
        CommentDto storedCommentDto = commentRepository.createComment(commentDto);
        commentInBoardRepository.addCommentInBoardCommentList(storedCommentDto);
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!commentInBoardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException("BOARD-401","Unknown board id : " + boardId);
        }
    }

    private void throwIfContentOverFlowThousand(String content){
        if (content.length() > 1000){
            throw new ContentOverFlowException();
        }
    }

    @Override
    public List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx){
        throwIfBoardDoesNotExist(boardId);
        throwIfInvalidRangeDetected(startIdx, endIdx);
        int maxIdx = commentRepository.getCommentMaxIdxByBoardId(boardId);
        endIdx = adjustEndIdx(startIdx, endIdx, maxIdx);
        List<CommentDto> commentDtoList = commentRepository.inquiryAllCommentByBoardId(boardId, startIdx, endIdx);
        return commentDtoList;
    }

    private void throwIfInvalidRangeDetected(int startIdx, int endIdx){
        if (startIdx > endIdx || startIdx < 1){
            throw new InvalidRangeException("BOARD-404","Invalid range from : " + startIdx + " to : " + endIdx);
        }
    }

    private int adjustEndIdx(int startIdx, int endIdx, int maxIdx){
        if (maxIdx < endIdx){
            endIdx = maxIdx;
        }
        if (endIdx - startIdx + 1 > PerPage.PER_PAGE){
            return startIdx + PerPage.PER_PAGE - 1;
        }
        return endIdx;
    }

    @Override
    public void modifyComment(CommentDto commentDto){
        throwIfCommentDoesNotExist(commentDto.getId());
        throwIfContentOverFlowThousand(commentDto.getContent());
        CommentDto storedCommentDto = commentRepository.inquiryCommentById(commentDto.getId());
        storedCommentDto.setContent(commentDto.getContent());
        throwIfBoardDoesNotExist(storedCommentDto.getBoardId());
        commentRepository.modifyComment(storedCommentDto);
    }

    private void throwIfCommentDoesNotExist(int commentId){
        if (!commentRepository.isExistComment(commentId)){
            throw new CommentDoesNotExistException("BOARD-406","Unknown comment id : " + commentId);
        }
    }

    @Override
    public void deleteComment(int commentId){
        throwIfCommentDoesNotExist(commentId);
        commentRepository.deleteComment(commentId);
    }

}
