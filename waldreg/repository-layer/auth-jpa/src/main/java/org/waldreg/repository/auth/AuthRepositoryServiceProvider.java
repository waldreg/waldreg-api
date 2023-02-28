package org.waldreg.repository.auth;

import org.springframework.transaction.annotation.Transactional;
import org.waldreg.domain.user.User;
import org.waldreg.repository.auth.mapper.AuthRepositoryMapper;
import org.waldreg.repository.auth.repository.JpaUserRepository;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.UnknownUserIdException;
import org.waldreg.token.spi.AuthRepository;

public class AuthRepositoryServiceProvider implements AuthRepository{

    private final JpaUserRepository jpaUserRepository;
    private final AuthRepositoryMapper authRepositoryMapper;

    public AuthRepositoryServiceProvider(JpaUserRepository jpaUserRepository, AuthRepositoryMapper authRepositoryMapper){
        this.jpaUserRepository = jpaUserRepository;
        this.authRepositoryMapper = authRepositoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public TokenUserDto findUserById(int id){
        User user = getUserById(id);
        return authRepositoryMapper.userToTokenUserDto(user);
    }

    private User getUserById(int id){
        return jpaUserRepository.findById(id).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user id \"" + id + "\"");}
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TokenUserDto findUserByBoardId(int boardId){
        User user = getUserByBoardId(boardId);
        return authRepositoryMapper.userToTokenUserDto(user);
    }

    private User getUserByBoardId(int boardId){
        return jpaUserRepository.findUserByBoardId(boardId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user by board id \"" + boardId + "\"");}
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TokenUserDto findUserByCommentId(int commentId){
        User user = getUserByCommentId(commentId);
        return authRepositoryMapper.userToTokenUserDto(user);
    }

    private User getUserByCommentId(int commentId){
        return jpaUserRepository.findUserByCommentId(commentId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user by comment id \"" + commentId + "\"");}
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TokenUserDto findUserByUserId(String userId){
        throwIfUnknownUserIdException(userId);
        User user = getUserByUserId(userId);
        return authRepositoryMapper.userToTokenUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistUserId(String userId){
        return jpaUserRepository.isExistUserByUserId(userId);
    }

    private void throwIfUnknownUserIdException(String userId){
        if (!jpaUserRepository.isExistUserByUserId(userId)){
            throw new UnknownUserIdException(userId);
        }
    }

    private User getUserByUserId(String userId){
        return jpaUserRepository.findUserByUserId(userId).orElseThrow(
                () -> {throw new IllegalStateException("Cannot find user user id \"" + userId + "\"");}
        );
    }


}
