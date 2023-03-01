package org.waldreg.repository.board;

import org.springframework.stereotype.Repository;
import org.waldreg.board.board.spi.BoardUserRepository;
import org.waldreg.board.comment.spi.CommentUserRepository;
import org.waldreg.board.dto.UserDto;
import org.waldreg.domain.user.User;
import org.waldreg.repository.board.mapper.BoardUserRepositoryMapper;
import org.waldreg.repository.board.repository.JpaUserRepository;

@Repository
public class BoardUserServiceProvider implements BoardUserRepository, CommentUserRepository{

    private final JpaUserRepository jpaUserRepository;
    private final BoardUserRepositoryMapper boardUserRepositoryMapper;

    public BoardUserServiceProvider(JpaUserRepository jpaUserRepository, BoardUserRepositoryMapper boardUserRepositoryMapper){
        this.jpaUserRepository = jpaUserRepository;
        this.boardUserRepositoryMapper = boardUserRepositoryMapper;
    }

    @Override
    public UserDto getUserInfo(int id){
        User user = getUserById(id);
        return boardUserRepositoryMapper.userToUserDto(user);
    }

    private User getUserById(int id){
        return jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
    }

}
