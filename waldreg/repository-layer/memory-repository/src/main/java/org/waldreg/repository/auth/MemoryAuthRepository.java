package org.waldreg.repository.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.domain.user.User;
import org.waldreg.repository.MemoryUserStorage;
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
    public User findUserByUserIdPw(String userId, String userPassword){

        User user = memoryUserStorage.readUserByUserId(userId);
        if(!user.getUserPassword().equals(userPassword)){
            throw new PasswordMissMatchException();
        }
        return user;
    }

    @Override
    public User findUserById(int id){
        User user = memoryUserStorage.readUserById(id);
        return user;
    }

}
