package org.waldreg.repository.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.token.dto.TokenUserDto;
import org.waldreg.token.exception.PasswordMissMatchException;
import org.waldreg.token.spi.AuthRepository;

@Repository
public class MemoryAuthRepository implements AuthRepository{

    private final MemoryUserStorage memoryUserStorage;

    @Autowired
    public MemoryAuthRepository(MemoryUserStorage memoryUserStorage){
        this.memoryUserStorage = memoryUserStorage;
    }

    @Override
    public TokenUserDto findUserByUserIdPw(String userId, String userPassword){
        User user = memoryUserStorage.readUserByUserId(userId);
        throwIfUserDoesNotExist(user, userPassword);
        TokenUserDto tokenUserDto = TokenUserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .build();
        return tokenUserDto;
    }

    private void throwIfUserDoesNotExist(User user, String userPassword){
        if (!user.getUserPassword().equals(userPassword)){
            throw new PasswordMissMatchException();
        }
    }

    @Override
    public TokenUserDto findUserById(int id){
        User user = memoryUserStorage.readUserById(id);
        TokenUserDto tokenUserDto = TokenUserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .userPassword(user.getUserPassword())
                .build();
        return tokenUserDto;
    }

}
