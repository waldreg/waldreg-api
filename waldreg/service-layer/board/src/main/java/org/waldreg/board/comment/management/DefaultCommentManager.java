package org.waldreg.board.comment.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.waldreg.board.comment.exception.BoardDoesNotExistException;
import org.waldreg.board.comment.exception.ContentLengthOverThousandException;
import org.waldreg.board.comment.exception.UserDoesNotExistException;
import org.waldreg.board.comment.spi.BoardRepository;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.comment.spi.UserRepository;
import org.waldreg.board.dto.CommentDto;

public class DefaultCommentManager implements CommentManager{

    private CommentRepository commentRepository;
    private UserRepository userRepository;

    private BoardRepository boardRepository;

    @Autowired
    public void DefaultBoardManager(CommentRepository commentRepository, UserRepository userRepository, BoardRepository boardRepository){
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }


    @Override
    public void CreateComment(CommentDto commentDto){
        throwIfUserDoesNotExist(commentDto.getUserDto().getId());
        throwIfBoardDoesNotExist(commentDto.getBoardId());
        throwIfContentOverFlowThousand(commentDto.getContent());
        CommentDto storedCommentDto = commentRepository.createComment(commentDto);
        boardRepository.addComment(storedCommentDto);
    }

    private void throwIfUserDoesNotExist(int authorId){
        if (!userRepository.isExistUser(authorId)){
            throw new UserDoesNotExistException(authorId);
        }
    }

    private void throwIfBoardDoesNotExist(int boardId){
        if (!boardRepository.isExistBoard(boardId)){
            throw new BoardDoesNotExistException(boardId);
        }
    }
    private void throwIfContentOverFlowThousand(String content){
        if (content.length() >1000){
            throw new ContentLengthOverThousandException();
        }
    }

}
