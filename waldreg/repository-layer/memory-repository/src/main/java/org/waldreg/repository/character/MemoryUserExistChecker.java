package org.waldreg.repository.character;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.waldreg.character.spi.UserExistChecker;
import org.waldreg.repository.MemoryUserStorage;
import org.waldreg.user.exception.UnknownIdException;

@Repository
public class MemoryUserExistChecker implements UserExistChecker{

    private final MemoryUserStorage memoryUserStorage;

    @Autowired
    public MemoryUserExistChecker(MemoryUserStorage memoryUserStorage){
        this.memoryUserStorage = memoryUserStorage;
    }

    @Override
    public boolean isExistUser(int id){
        try{
            memoryUserStorage.readUserById(id);
            return true;
        } catch (UnknownIdException ignored){
            return false;
        }
    }

}
