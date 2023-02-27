package org.waldreg.repository.board;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.waldreg.board.comment.spi.CommentRepository;
import org.waldreg.board.dto.CommentDto;
import org.waldreg.domain.board.Board;
import org.waldreg.domain.board.comment.Comment;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.CommentRepositoryMapper;
import org.waldreg.repository.board.repository.JpaBoardRepository;
import org.waldreg.repository.board.repository.JpaCommentRepository;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Repository
public class CommentRepositoryServiceProvider implements CommentRepository{

    private final CommentRepositoryMapper commentRepositoryMapper;
    private final JpaCommentRepository jpaCommentRepository;
    private final JpaUserRepository jpaUserRepository;
    private final JpaBoardRepository jpaBoardRepository;

    public CommentRepositoryServiceProvider(CommentRepositoryMapper commentRepositoryMapper, JpaCommentRepository jpaCommentRepository, JpaUserRepository jpaUserRepository, JpaBoardRepository jpaBoardRepository){
        this.commentRepositoryMapper = commentRepositoryMapper;
        this.jpaCommentRepository = jpaCommentRepository;
        this.jpaUserRepository = jpaUserRepository;
        this.jpaBoardRepository = jpaBoardRepository;
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto){
        Comment comment = commentRepositoryMapper.commentDtoToCommentDomain(commentDto);
        comment.setBoard(getBoardById(commentDto.getBoardId()));
        comment.setUser(getUserById(commentDto.getUserDto().getId()));
        jpaCommentRepository.save(comment);
        return commentRepositoryMapper.commentDomainToCommentDto(comment);
    }

    private Board getBoardById(int boardId){
        return jpaBoardRepository.findById(boardId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find board id \"" + boardId + "\"");}
        );
    }

    private User getUserById(int id){
        return jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
    }

    @Override
    @Transactional(readOnly = true)
    public int getCommentMaxIdxByBoardId(int boardId){
        return jpaCommentRepository.getBoardMaxIdxByBoardId(boardId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> inquiryAllCommentByBoardId(int boardId, int startIdx, int endIdx){
        List<Comment> commentList = jpaCommentRepository.findAllByBoardId(boardId, startIdx - 1, endIdx - startIdx + 1);
        return commentRepositoryMapper.commentDomainListToCommentDtoList(commentList);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto inquiryCommentById(int commentId){
        Comment comment = getCommentById(commentId);
        return commentRepositoryMapper.commentDomainToCommentDto(comment);
    }

    @Override
    @Transactional
    public void modifyComment(CommentDto commentDto){
        Comment comment = getCommentById(commentDto.getId());
        comment.setContent(commentDto.getContent());
        comment.setLastModifiedAt(LocalDateTime.now());
        jpaCommentRepository.save(comment);
    }

    private Comment getCommentById(int id){
        return jpaCommentRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find comment id \"" + id + "\"");}
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistComment(int commentId){
        return jpaCommentRepository.existsById(commentId);
    }

    @Override
    @Transactional
    public void deleteComment(int id){
        jpaCommentRepository.deleteById(id);
    }

}
